package com.application.strms.domain.repository;

import com.application.strms.domain.model.Email;
import com.application.strms.domain.model.User;
import com.application.strms.domain.model.UserAuth;
import java.io.IOException;

public interface UserRepository {
    User findByEmail(Email email);

    UserAuth findAuthByEmail(Email email) throws IOException;

    void addUser(User user, UserAuth userAuth) throws IOException;
}