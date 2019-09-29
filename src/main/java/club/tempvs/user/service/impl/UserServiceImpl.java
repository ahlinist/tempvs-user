package club.tempvs.user.service.impl;

import club.tempvs.user.dao.UserRepository;
import club.tempvs.user.domain.User;
import club.tempvs.user.exception.UserAlreadyExistsException;
import club.tempvs.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User register(String email, String password) {
        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new UserAlreadyExistsException();
        }

        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(email, encodedPassword);
        return userRepository.save(user);
    }
}
