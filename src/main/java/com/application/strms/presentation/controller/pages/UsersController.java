package com.application.strms.presentation.controller.pages;

import java.util.List;

import com.application.strms.domain.model.User;
import com.application.strms.domain.repository.UserRepository;
import com.application.strms.presentation.controller.BaseController;
import com.application.strms.presentation.service.UiConstants;
import com.application.strms.presentation.service.UiUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

public class UsersController extends BaseController {
    @FXML
    private TableView<User> usersTable;
    @FXML
    private Button addUserButton;
    @FXML
    private Button editButton;
    @FXML
    private Label emptyStateLabel;

    @FXML
    public void initialize() {
        editButton.setDisable(true);
        usersTable.getSelectionModel().selectedItemProperty()
                .addListener((_, _, newValue) -> editButton.setDisable(newValue == null));
    }

    @Override
    protected void onReady() {
        if (context != null && context.getSessionManager().isAuthenticated()) {
            User currentUser = context.getSessionManager().getCurrentUser();
            if (currentUser.getRole().canManageUsers()) {
                UiUtils.setVisibility(addUserButton, true);
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
                usersTable.getItems().addAll(users);
                UiUtils.setVisibility(usersTable, true);
                UiUtils.setVisibility(emptyStateLabel, false);
            }
        } catch (Exception e) {
            showEmptyState();
        }
    }

    private void showEmptyState() {
        UiUtils.setVisibility(usersTable, false);
        UiUtils.setVisibility(emptyStateLabel, true);
    }

    @FXML
    protected void handleEdit() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            navigateToUpdateUser(selectedUser);
        }
    }

    @FXML
    protected void goToAddUser() {
        navigator.goTo(UiConstants.Pages.ADD_USER);
    }

    @FXML
    protected void goBack() {
        navigator.goTo(UiConstants.Pages.HOME);
    }

    private void navigateToUpdateUser(User user) {
        try {
            navigator.goTo(UiConstants.Pages.UPDATE_USER, controller -> {
                if (controller instanceof UpdateUserController updateController) {
                    updateController.setUserToUpdate(user);
                }
            });
        } catch (Exception e) {
            System.err.println("Error navigating to UpdateUser: " + e.getMessage());
        }
    }
}
