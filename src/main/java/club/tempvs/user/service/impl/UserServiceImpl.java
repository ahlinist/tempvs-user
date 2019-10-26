package club.tempvs.user.service.impl;

import club.tempvs.user.dao.EmailVerificationDao;
import club.tempvs.user.dao.UserDao;
import club.tempvs.user.domain.EmailVerification;
import club.tempvs.user.domain.User;
import club.tempvs.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationDao emailVerificationDao;

    @Override
    @Transactional
    public User register(String verificationId, String password) {
        EmailVerification emailVerification = emailVerificationDao.get(verificationId)
                .orElseThrow(NoSuchElementException::new);
        String email = emailVerification.getEmail();
        emailVerificationDao.delete(emailVerification);

        if (userDao.get(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, email);
        }

        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(email, encodedPassword);
        return userDao.save(user);
    }
}
