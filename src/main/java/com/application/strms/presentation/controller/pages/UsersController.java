package com.application.strms.presentation.controller.pages;

import java.util.List;

import com.application.strms.application.result.DeleteUserResult;
import com.application.strms.application.service.AuthService;
import com.application.strms.domain.model.User;
import com.application.strms.domain.repository.UserRepository;
import com.application.strms.presentation.controller.BaseController;
import com.application.strms.presentation.model.UserDisplay;
import com.application.strms.presentation.service.UiConstants;
import com.application.strms.presentation.service.UiUtils;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

public class UsersController extends BaseController {
    @FXML
    private TableView<UserDisplay> usersTable;
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

        addDeleteButtonColumn();
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
                users.stream()
                        .map(UserDisplay::new)
                        .forEach(userDisplay -> usersTable.getItems().add(userDisplay));
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
        UserDisplay selectedUserDisplay = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUserDisplay != null) {
            navigateToUpdateUser(selectedUserDisplay.getUser());
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

    private void addDeleteButtonColumn() {
        TableColumn<UserDisplay, Void> deleteColumn = new TableColumn<>("Actions");
        deleteColumn.setPrefWidth(100);
        deleteColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.getStyleClass().add("delete-button");
                deleteButton.setOnAction(event -> {
                    UserDisplay userDisplay = getTableView().getItems().get(getIndex());
                    if (userDisplay != null) {
                        handleDelete(userDisplay.getUser());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox();
                    hbox.setAlignment(Pos.CENTER);
                    hbox.setSpacing(10);
                    hbox.getChildren().add(deleteButton);
                    setGraphic(hbox);
                }
            }
        });

        usersTable.getColumns().add(deleteColumn);
    }

    private void handleDelete(User user) {
        try {
            User currentUser = context.getSessionManager().getCurrentUser();
            if (currentUser == null) {
                showDeleteError("Not authenticated");
                return;
            }

            AuthService authService = context.getAuthService();
            DeleteUserResult result = authService.deleteUser(currentUser, user.getId());

            if (result.isSuccess()) {
                navigator.notify("User succesfully deleted !");
                usersTable.getItems().remove(user);
                if (usersTable.getItems().isEmpty()) {
                    showEmptyState();
                }
            } else {
                showDeleteError(result.getError());
            }
        } catch (Exception e) {
            showDeleteError("Error deleting user: " + e.getMessage());
        }
    }

    private void showDeleteError(String message) {
        navigator.notify("Error: " + message);
    }
}
