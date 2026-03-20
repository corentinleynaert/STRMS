package com.application.strms.presentation.model;

import com.application.strms.domain.model.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UserDisplay {
    private final User user;
    private final StringProperty name;
    private final StringProperty email;
    private final StringProperty role;

    public UserDisplay(User user) {
        this.user = user;
        this.name = new SimpleStringProperty(user.getName());
        this.email = new SimpleStringProperty(user.getEmail().toString());
        this.role = new SimpleStringProperty(user.getRole().getIdentifier());
    }

    public User getUser() {
        return user;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public String getRole() {
        return role.get();
    }

    public StringProperty roleProperty() {
        return role;
    }
}
