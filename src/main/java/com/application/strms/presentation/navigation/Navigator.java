package com.application.strms.presentation.navigation;

import com.application.strms.application.ApplicationContext;
import com.application.strms.presentation.controller.BaseController;
import com.application.strms.presentation.controller.LayoutController;
import com.application.strms.presentation.loader.ViewLoader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;

public class Navigator {

    private final ApplicationContext context;
    private final LayoutController layout;

    public Navigator(LayoutController layout, ApplicationContext context) {
        this.layout = layout;
        this.context = context;
    }

    public void goTo(String viewName) {
        try {
            FXMLLoader loader = ViewLoader.load(viewName);
            Node view = loader.load();

            Object controller = loader.getController();
            if (controller instanceof BaseController baseController) {
                baseController.setApplicationContext(context);
                baseController.setNavigator(this);
            }

            layout.setContent(view);
        } catch (IOException e) {
            throw new RuntimeException("Navigation failed for view: " + viewName, e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error during navigation to: " + viewName, e);
        }
    }

    public void notify(String message) {
        layout.showNotification(message);
    }
}