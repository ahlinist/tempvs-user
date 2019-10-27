package club.tempvs.user.controller;

import club.tempvs.user.component.CookieHelper;
import club.tempvs.user.domain.User;
import club.tempvs.user.dto.CredentialsDto;
import club.tempvs.user.dto.UserDto;
import club.tempvs.user.dto.validation.Scope;
import club.tempvs.user.service.EmailVerificationService;
import club.tempvs.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final ConversionService mvcConversionService;
    private final CookieHelper cookieHelper;
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/register")
    public void register(@RequestBody @Validated(Scope.Register.class) CredentialsDto credentialsDto) {
        emailVerificationService.create(credentialsDto.getEmail());
    }

    @PostMapping("/verify/{verificationId}")
    public UserDto verify(
            @PathVariable String verificationId,
            @RequestBody @Validated(Scope.Verify.class) CredentialsDto credentialsDto,
            HttpServletResponse response) {
        User user = userService.register(verificationId, credentialsDto.getPassword());
        Cookie authCookie = cookieHelper.buildAuthCookie(user);
        response.addCookie(authCookie);
        return mvcConversionService.convert(user, UserDto.class);
    }

    @PostMapping("/login")
    public void login(@RequestBody @Validated(Scope.Login.class) CredentialsDto credentialsDto,
                      HttpServletResponse response) {
        String email = credentialsDto.getEmail();
        String password = credentialsDto.getPassword();
        User user = userService.login(email, password);
        Cookie authCookie = cookieHelper.buildAuthCookie(user);
        response.addCookie(authCookie);
    }
}
