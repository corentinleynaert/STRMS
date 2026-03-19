package com.application.strms.presentation.navigation;

import com.application.strms.application.ApplicationContext;
import com.application.strms.presentation.controller.BaseController;
import com.application.strms.presentation.loader.ViewLoader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Navigator {

    private final Stage stage;
    private final ApplicationContext context;

    public Navigator(Stage stage, ApplicationContext context) {
        this.stage = stage;
        this.context = context;
    }

    public void goTo(String viewName) {
        try {
            FXMLLoader loader = ViewLoader.load(viewName);
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof BaseController baseController) {
                baseController.setApplicationContext(context);
                baseController.setNavigator(this);
            }

            Scene scene = stage.getScene();

            if (scene == null) {
                scene = new Scene(root, 800, 600);
                stage.setScene(scene);
            } else {
                scene.setRoot(root);
            }

            stage.show();

        } catch (IOException e) {
            throw new RuntimeException("Navigation failed for view: " + viewName, e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error during navigation to: " + viewName, e);
        }
    }
}