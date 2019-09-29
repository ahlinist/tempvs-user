package club.tempvs.user.controller;

import club.tempvs.user.component.CookieHelper;
import club.tempvs.user.domain.User;
import club.tempvs.user.dto.RegisterDto;
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
    public void register(@RequestBody @Validated(Scope.Register.class) RegisterDto registerDto) {
        emailVerificationService.create(registerDto.getEmail());
    }

    @PostMapping("/verify/{verificationId}")
    public UserDto verify(
            @PathVariable String verificationId,
            @RequestBody @Validated(Scope.Verify.class) RegisterDto registerDto,
            HttpServletResponse response) {
        User user = userService.register(verificationId, registerDto.getPassword());
        Cookie authCookie = cookieHelper.buildAuthCookie(user);
        response.addCookie(authCookie);
        return mvcConversionService.convert(user, UserDto.class);
    }
}
