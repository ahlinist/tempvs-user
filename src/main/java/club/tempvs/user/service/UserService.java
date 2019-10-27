package club.tempvs.user.service;

import club.tempvs.user.domain.User;

public interface UserService {

    User register(String verificationId, String password);

    User login(String email, String password);
}
