package com.application.strms.domain.repository;

import com.application.strms.domain.model.Email;
import com.application.strms.domain.model.Ulid;
import com.application.strms.domain.model.User;
import com.application.strms.domain.model.UserAuth;
import java.io.IOException;
import java.util.List;

public interface UserRepository {
    User findByEmail(Email email);

    User findById(Ulid id);

    UserAuth findAuthByEmail(Email email) throws IOException;

    void addUser(User user, UserAuth userAuth) throws IOException;

    void updateUser(User user, UserAuth userAuth) throws IOException;

    List<User> getAllUsers();
}