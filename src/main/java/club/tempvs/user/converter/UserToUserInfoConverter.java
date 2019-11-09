package club.tempvs.user.converter;

import club.tempvs.user.domain.User;
import club.tempvs.user.dto.UserInfoDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserToUserInfoConverter implements Converter<User, String> {

    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public String convert(User source) {
        UserInfoDto target = new UserInfoDto();
        BeanUtils.copyProperties(source, target);
        target.setUserId(source.getId());
        return objectMapper.writeValueAsString(target);
    }
}
