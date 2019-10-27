package club.tempvs.user.dto;

import club.tempvs.user.dto.validation.Scope;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

@Data
public class CredentialsDto {

    @Email(groups = {Scope.Register.class, Scope.Login.class})
    @Null(groups = Scope.Verify.class)
    @NotBlank(groups = {Scope.Register.class, Scope.Login.class})
    private String email;
    @Null(groups = Scope.Register.class)
    @NotBlank(groups = {Scope.Verify.class, Scope.Login.class})
    private String password;
}
