package club.tempvs.user.controller;

import club.tempvs.user.amqp.EmailEventProcessor;
import club.tempvs.user.domain.User;
import club.tempvs.user.dto.RegisterDto;
import club.tempvs.user.repository.EmailVerificationRepository;
import club.tempvs.user.domain.EmailVerification;
import club.tempvs.user.dto.TempvsPrincipal;
import club.tempvs.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.messaging.Message;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class UserControllerIntegrationTest {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN = "df41895b9f26094d0b1d39b7bdd9849e"; //security_token as MD5

    private static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @Autowired
    private EmailVerificationRepository emailVerificationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageCollector messageCollector;
    @Autowired
    private EmailEventProcessor emailEventProcessor;

    @Test
    public void testRegister() throws Exception {
        File registerFile = ResourceUtils.getFile("classpath:user/register.json");
        String registerJson = new String(Files.readAllBytes(registerFile.toPath()));

        mvc.perform(post("/api/register")
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .content(registerJson)
                .header(AUTHORIZATION_HEADER, TOKEN))
                    .andExpect(status().isOk());
    }

    @Test
    public void testRegisterForExistingUser() throws Exception {
        File registerFile = ResourceUtils.getFile("classpath:user/register.json");
        String registerJson = new String(Files.readAllBytes(registerFile.toPath()));

        RegisterDto registerDto = mapper.readValue(registerFile, RegisterDto.class);

        User user = new User(registerDto.getEmail(), "password");
        userRepository.save(user);

        mvc.perform(post("/api/register")
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .content(registerJson)
                .header(AUTHORIZATION_HEADER, TOKEN))
                .andExpect(status().isConflict());
    }

    @Test
    public void testRegisterForInvalidPayload() throws Exception {
        mvc.perform(post("/api/register")
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .content("{\"email\": \"asd.com\"}")
                .header(AUTHORIZATION_HEADER, TOKEN))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testVerifyForMissingVerification() throws Exception {
        File createUserFile = ResourceUtils.getFile("classpath:user/verify.json");
        String createUserJson = new String(Files.readAllBytes(createUserFile.toPath()));

        mvc.perform(post("/api/verify")
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .content(createUserJson)
                .header(AUTHORIZATION_HEADER, TOKEN))
                    .andExpect(status().isNotFound());
    }

    @Test
    public void testVerify() throws Exception {
        String verificationId = "verificationId";
        String email = "test@email.com";
        EmailVerification emailVerification = new EmailVerification(email, verificationId);
        emailVerificationRepository.saveAndFlush(emailVerification);

        File createUserFile = ResourceUtils.getFile("classpath:user/verify.json");
        String createUserJson = new String(Files.readAllBytes(createUserFile.toPath()));

        mvc.perform(post("/api/verify/" + verificationId)
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .content(createUserJson)
                .header(AUTHORIZATION_HEADER, TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("email", is(email)))
                    .andExpect(jsonPath("currentProfileId", isEmptyOrNullString()))
                    .andExpect(jsonPath("timeZone", isEmptyOrNullString()))
                    .andExpect(header().string("Set-Cookie", containsString("TEMPVS_AUTH=")));

        Message<String> received = (Message<String>) messageCollector.forChannel(emailEventProcessor.send()).poll();
        assertThat(received.getPayload(), containsString("test@email.com"));
        assertThat(received.getPayload(), containsString("Registration at Tempvs"));
        assertThat(received.getPayload(), containsString("Greetings at Tempvs! To finish your registration follow the link below(valid for 24 hours):"));
        assertThat(received.getPayload(), containsString("http://localhost:8080/user/registration/"));
    }
}
