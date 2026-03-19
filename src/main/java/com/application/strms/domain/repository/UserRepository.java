package com.application.strms.domain.repository;

import com.application.strms.domain.model.Email;
import com.application.strms.domain.model.User;

public interface UserRepository {
    User findByEmail(Email email);
    void addUser(User user);
}