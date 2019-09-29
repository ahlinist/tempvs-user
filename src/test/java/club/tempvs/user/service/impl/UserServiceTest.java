package club.tempvs.user.service.impl;

import club.tempvs.user.dao.UserRepository;
import club.tempvs.user.domain.User;
import club.tempvs.user.exception.UserAlreadyExistsException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private User user;

    @Test
    public void testRegister() {
        String email = "test@email.com";
        String password = "password";
        String encodedPassword = "encoded password";
        User preparedUser = new User(email, encodedPassword);

        when(userRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.empty());
        when(bCryptPasswordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userRepository.save(preparedUser)).thenReturn(user);

        User result = userService.register(email, password);

        verify(userRepository).findByEmailIgnoreCase(email);
        verify(bCryptPasswordEncoder).encode(password);
        verify(userRepository).save(preparedUser);
        verifyNoMoreInteractions(bCryptPasswordEncoder, userRepository);

        assertEquals("User object is returned", user, result);
    }

    @Test(expected = UserAlreadyExistsException.class)
    public void testRegisterForDuplicate() {
        String email = "test@email.com";
        String password = "password";

        when(userRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(user));

        userService.register(email, password);
    }
}
