package club.tempvs.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailDto {
    
    private String email;
    private String subject;
    private String body;
}
