package club.tempvs.user.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import java.time.Instant;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String email;
    private String verificationId;
    @CreatedDate
    private Instant createdDate;

    public EmailVerification(String email, String verificationId) {
        this.email = email;
        this.verificationId = verificationId;
    }
}
