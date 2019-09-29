package club.tempvs.user.converter;

import club.tempvs.user.domain.User;
import club.tempvs.user.dto.UserDto;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserToUserDtoConverter implements Converter<User, UserDto> {

    @Override
    public UserDto convert(User source) {
        UserDto target = new UserDto();
        BeanUtils.copyProperties(source, target);
        return target;
    }
}
