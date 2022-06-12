package club.tempvs.user.controller;

import club.tempvs.user.domain.User;
import club.tempvs.user.dto.CredentialsDto;
import club.tempvs.user.dto.validation.Scope;
import club.tempvs.user.service.EmailVerificationService;
import club.tempvs.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class UserController {

    private static final String REFRESH_COOKIES_HEADER = "Tempvs-Refresh-Cookies";
    private static final String LOGOUT_HEADER = "Tempvs-Logout";

    private final UserService userService;
    private final ConversionService mvcConversionService;
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/register")
    public void register(@RequestBody @Validated(Scope.Register.class) CredentialsDto credentialsDto) {
        emailVerificationService.create(credentialsDto.getEmail());
    }

    @PostMapping("/verify/{verificationId}")
    public void verify(
            @PathVariable String verificationId,
            @RequestBody @Validated(Scope.Verify.class) CredentialsDto credentialsDto,
            HttpServletResponse response) {
        User user = userService.register(verificationId, credentialsDto.getPassword());
        String userInfo = mvcConversionService.convert(user, String.class);
        response.addHeader(REFRESH_COOKIES_HEADER, userInfo);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.FOUND)
    public void login(@RequestBody @Validated(Scope.Login.class) CredentialsDto credentialsDto,
                      HttpServletRequest request, HttpServletResponse response) {
        String referer = request.getHeader(HttpHeaders.REFERER);
        String email = credentialsDto.getEmail();
        String password = credentialsDto.getPassword();
        User user = userService.login(email, password);
        String userInfo = mvcConversionService.convert(user, String.class);
        response.addHeader(REFRESH_COOKIES_HEADER, userInfo);
        response.addHeader(HttpHeaders.LOCATION, referer);
    }

    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        response.addHeader(LOGOUT_HEADER, "");
    }
}
