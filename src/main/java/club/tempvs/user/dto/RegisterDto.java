package club.tempvs.user.dto;

import club.tempvs.user.dto.validation.Scope;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
public class RegisterDto {

    @Email(groups = Scope.Register.class)
    @Null(groups = Scope.Verify.class)
    @NotNull(groups = Scope.Register.class)
    private String email;
    @Null(groups = Scope.Register.class)
    @NotNull(groups = Scope.Verify.class)
    private String password;
}
