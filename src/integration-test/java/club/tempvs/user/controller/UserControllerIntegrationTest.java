package club.tempvs.user.controller;

import club.tempvs.user.amqp.EmailEventProcessor;
import club.tempvs.user.domain.User;
import club.tempvs.user.dto.CredentialsDto;
import club.tempvs.user.dto.TempvsPrincipal;
import club.tempvs.user.repository.EmailVerificationRepository;
import club.tempvs.user.domain.EmailVerification;
import club.tempvs.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.messaging.Message;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.Locale;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class UserControllerIntegrationTest {

    private static final String REFRESH_COOKIES_HEADER = "Tempvs-Refresh-Cookies";
    private static final String LOGOUT_HEADER = "Tempvs-Logout";
    private static final String USER_INFO_HEADER = "User-Info";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN = "df41895b9f26094d0b1d39b7bdd9849e"; //security_token as MD5

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private EmailVerificationRepository emailVerificationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageCollector messageCollector;
    @Autowired
    private EmailEventProcessor emailEventProcessor;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testRegister() throws Exception {
        File registerFile = ResourceUtils.getFile("classpath:user/register.json");
        String registerJson = new String(Files.readAllBytes(registerFile.toPath()));

        mvc.perform(post("/register")
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

        CredentialsDto credentialsDto = objectMapper.readValue(registerFile, CredentialsDto.class);

        User user = new User(credentialsDto.getEmail(), "password");
        userRepository.save(user);

        mvc.perform(post("/register")
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .content(registerJson)
                .header(AUTHORIZATION_HEADER, TOKEN))
                .andExpect(status().isConflict());
    }

    @Test
    public void testRegisterForInvalidPayload() throws Exception {
        mvc.perform(post("/register")
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

        mvc.perform(post("/verify")
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

        mvc.perform(post("/verify/" + verificationId)
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .content(createUserJson)
                .header(AUTHORIZATION_HEADER, TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(header().exists(REFRESH_COOKIES_HEADER));

        Message<String> received = (Message<String>) messageCollector.forChannel(emailEventProcessor.send()).poll();
        assertThat(received.getPayload(), containsString("test@email.com"));
        assertThat(received.getPayload(), containsString("Registration at Tempvs"));
        assertThat(received.getPayload(), containsString("Greetings at Tempvs! To finish your registration follow the link below(valid for 24 hours):"));
        assertThat(received.getPayload(), containsString("http://localhost:8080/user/registration/"));
    }

    @Test
    public void testLogin() throws Exception {
        File loginFile = ResourceUtils.getFile("classpath:user/login.json");
        String loginJson = new String(Files.readAllBytes(loginFile.toPath()));
        String referer = "https://my-host.com";

        CredentialsDto credentialsDto = objectMapper.readValue(loginFile, CredentialsDto.class);

        User user = new User(credentialsDto.getEmail(), passwordEncoder.encode(credentialsDto.getPassword()));
        userRepository.save(user);

        mvc.perform(post("/login")
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .content(loginJson)
                .header(AUTHORIZATION_HEADER, TOKEN)
                .header("Referer", referer))
                    .andExpect(status().isFound())
                    .andExpect(header().exists(REFRESH_COOKIES_HEADER))
                    .andExpect(header().string("Location", referer));
    }

    @Test
    public void testLoginForUnexistingUser() throws Exception {
        File loginFile = ResourceUtils.getFile("classpath:user/login.json");
        String loginJson = new String(Files.readAllBytes(loginFile.toPath()));

        User user = new User("some@email.com", "no matter what password");
        userRepository.save(user);

        mvc.perform(post("/login")
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .content(loginJson)
                .header(AUTHORIZATION_HEADER, TOKEN))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testLoginForWrongCredentials() throws Exception {
        File loginFile = ResourceUtils.getFile("classpath:user/login.json");
        String loginJson = new String(Files.readAllBytes(loginFile.toPath()));

        CredentialsDto credentialsDto = objectMapper.readValue(loginFile, CredentialsDto.class);

        User user = new User(credentialsDto.getEmail(), "some wrong password");
        userRepository.save(user);

        mvc.perform(post("/login")
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .content(loginJson)
                .header(AUTHORIZATION_HEADER, TOKEN))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testLoginForInvalidEmail() throws Exception {

        CredentialsDto credentialsDto = new CredentialsDto("invalidemail", "password");
        String jsonString = objectMapper.writeValueAsString(credentialsDto);

        mvc.perform(post("/login")
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .content(jsonString)
                .header(AUTHORIZATION_HEADER, TOKEN))
                .andExpect(status().isBadRequest());

        credentialsDto = new CredentialsDto("", "");
        jsonString = objectMapper.writeValueAsString(credentialsDto);

        mvc.perform(post("/login")
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .content(jsonString)
                .header(AUTHORIZATION_HEADER, TOKEN))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testLogout() throws Exception {
        String userInfoValue = buildUserInfoValue(1L);

        mvc.perform(post("/logout")
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .header(USER_INFO_HEADER, userInfoValue)
                .header(AUTHORIZATION_HEADER, TOKEN))
                .andExpect(status().isOk())
                .andExpect(header().exists(LOGOUT_HEADER));
    }

    private String buildUserInfoValue(Long id) throws Exception {
        TempvsPrincipal userInfo = new TempvsPrincipal();
        userInfo.setUserId(id);
        userInfo.setLang(Locale.ENGLISH.getLanguage());
        return objectMapper.writeValueAsString(userInfo);
    }
}
