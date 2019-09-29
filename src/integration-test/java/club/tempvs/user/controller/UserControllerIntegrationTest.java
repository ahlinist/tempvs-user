package club.tempvs.user.controller;

import club.tempvs.user.dto.TempvsPrincipal;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class UserControllerIntegrationTest {

    private static final String USER_INFO_HEADER = "User-Info";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN = "df41895b9f26094d0b1d39b7bdd9849e"; //security_token as MD5

    private static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @Test
    public void testGetWelcomePage() throws Exception {
        Long id = 1L;
        String userInfoValue = buildUserInfoValue(id);
        File createUserFile = ResourceUtils.getFile("classpath:user/create.json");
        String createUserJson = new String(Files.readAllBytes(createUserFile.toPath()));

        mvc.perform(post("/api/register")
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .content(createUserJson)
                .header(AUTHORIZATION_HEADER, TOKEN))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("email", is("test@email.com")))
                    .andExpect(jsonPath("currentProfileId", isEmptyOrNullString()))
                    .andExpect(jsonPath("timeZone", isEmptyOrNullString()))
                    .andExpect(header().string("Set-Cookie", containsString("TEMPVS_AUTH=")));
    }

    private String buildUserInfoValue(Long id) throws Exception {
        TempvsPrincipal principal = new TempvsPrincipal();
        principal.setUserId(id);
        principal.setLang("en");
        return mapper.writeValueAsString(principal);
    }
}
