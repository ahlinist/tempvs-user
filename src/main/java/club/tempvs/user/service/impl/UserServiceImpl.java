package club.tempvs.user.service.impl;

import club.tempvs.user.dao.UserRepository;
import club.tempvs.user.domain.EmailVerification;
import club.tempvs.user.domain.User;
import club.tempvs.user.exception.UserAlreadyExistsException;
import club.tempvs.user.service.EmailVerificationService;
import club.tempvs.user.service.UserService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;

    @Override
    @Transactional
    public User register(String verificationId, String password) {
        EmailVerification emailVerification = emailVerificationService.get(verificationId);
        String email = emailVerification.getEmail();
        emailVerificationService.delete(emailVerification);

        if (find(email).isPresent()) {
            throw new UserAlreadyExistsException();
        }

        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(email, encodedPassword);
        return save(user);
    }

    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")
    })
    private User save(User user) {
        return userRepository.save(user);
    }

    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")
    })
    private Optional<User> find(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }
}
