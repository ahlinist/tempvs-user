package club.tempvs.user.converter;

import club.tempvs.user.domain.User;
import club.tempvs.user.dto.AuthCookieDto;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserToAuthCookieConverter implements Converter<User, AuthCookieDto> {

    @Override
    public AuthCookieDto convert(User source) {
        AuthCookieDto target = new AuthCookieDto();
        BeanUtils.copyProperties(source, target);
        target.setUserId(source.getId());
        return target;
    }
}
