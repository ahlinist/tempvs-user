package club.tempvs.user.dao;

import club.tempvs.user.domain.User;

import java.util.Optional;

public interface UserDao {

    User save(User user);

    Optional<User> get(String email);
}
