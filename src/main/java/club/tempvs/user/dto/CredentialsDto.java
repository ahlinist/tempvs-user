package club.tempvs.user.dto;

import club.tempvs.user.dto.validation.Scope;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CredentialsDto {

    @Email(groups = {Scope.Register.class, Scope.Login.class})
    @Null(groups = Scope.Verify.class)
    @NotBlank(groups = {Scope.Register.class, Scope.Login.class})
    private String email;
    @Null(groups = Scope.Register.class)
    @NotBlank(groups = {Scope.Verify.class, Scope.Login.class})
    private String password;
}
