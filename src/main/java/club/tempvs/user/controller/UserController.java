package club.tempvs.user.controller;

import club.tempvs.user.domain.User;
import club.tempvs.user.dto.UserDto;
import club.tempvs.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final ConversionService mvcConversionService;

    @PostMapping
    @RequestMapping("/register")
    public UserDto register(@RequestBody @Validated UserDto userDto) {
        User user = userService.register(userDto.getEmail(), userDto.getPassword());
        return mvcConversionService.convert(user, UserDto.class);
    }
}
