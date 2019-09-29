package club.tempvs.user.converter;

import club.tempvs.user.domain.User;
import club.tempvs.user.dto.TempvsPrincipal;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserToPrincipalConverter implements Converter<User, TempvsPrincipal> {

    @Override
    public TempvsPrincipal convert(User source) {
        TempvsPrincipal target = new TempvsPrincipal();
        BeanUtils.copyProperties(source, target);
        return target;
    }
}
