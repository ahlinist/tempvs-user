package club.tempvs.user.service.impl;

import club.tempvs.user.dao.EmailVerificationDao;
import club.tempvs.user.dao.UserDao;
import club.tempvs.user.domain.EmailVerification;
import club.tempvs.user.domain.User;
import club.tempvs.user.exception.UnauthorizedException;
import club.tempvs.user.exception.UserAlreadyExistsException;
import club.tempvs.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw new UserAlreadyExistsException(email);
        }

        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(email, encodedPassword);
        return userDao.save(user);
    }

    @Override
    @Transactional
    public User login(String email, String password) {
        User user = userDao.get(email)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("Wrong credentials");
        }

        return user;
    }
}
