package com.application.strms.presentation.controller.pages;

import java.util.List;

import com.application.strms.domain.model.User;
import com.application.strms.domain.repository.UserRepository;
import com.application.strms.presentation.controller.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

public class UsersController extends BaseController {
    @FXML
    private TableView<User> users_table;
    @FXML
    private Button add_user_button;
    @FXML
    private Button edit_button;
    @FXML
    private Label empty_state;

    @FXML
    public void initialize() {
        edit_button.setDisable(true);
        users_table.getSelectionModel().selectedItemProperty()
                .addListener((_, _, newValue) -> edit_button.setDisable(newValue == null));
    }

    @Override
    protected void onReady() {
        if (context != null && context.getSessionManager().isAuthenticated()) {
            User currentUser = context.getSessionManager().getCurrentUser();
            if (currentUser.isAdmin()) {
                add_user_button.setVisible(true);
                add_user_button.setManaged(true);
            }
            loadUsers();
        }
    }

    private void loadUsers() {
        try {
            UserRepository userRepository = context.getApplicationUserRepository();
            List<User> users = userRepository.getAllUsers();

            if (users == null || users.isEmpty()) {
                showEmptyState();
            } else {
                users_table.getItems().addAll(users);
                users_table.setVisible(true);
                users_table.setManaged(true);
                empty_state.setVisible(false);
                empty_state.setManaged(false);
            }
        } catch (Exception e) {
            showEmptyState();
        }
    }

    private void showEmptyState() {
        users_table.setVisible(false);
        users_table.setManaged(false);
        empty_state.setVisible(true);
        empty_state.setManaged(true);
    }

    @FXML
    protected void handleEdit() {
        User selectedUser = users_table.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            navigateToUpdateUser(selectedUser);
        }
    }

    @FXML
    protected void goToAddUser() {
        navigator.goTo("AddUser");
    }

    @FXML
    protected void goBack() {
        navigator.goTo("Home");
    }

    private void navigateToUpdateUser(User user) {
        try {
            navigator.goTo("UpdateUser", controller -> {
                if (controller instanceof UpdateUserController updateController) {
                    updateController.setUserToUpdate(user);
                }
            });
        } catch (Exception e) {
            System.err.println("Error navigating to UpdateUser: " + e.getMessage());
        }
    }
}
