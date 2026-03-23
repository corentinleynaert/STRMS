package com.strms.presentation.navigation;

import com.strms.application.ApplicationContext;
import com.strms.presentation.controller.BaseController;
import com.strms.presentation.controller.LayoutController;
import com.strms.presentation.loader.ViewLoader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;
import java.util.function.Consumer;

public class Navigator {
    private final ApplicationContext context;
    private final LayoutController layout;

    public Navigator(LayoutController layout, ApplicationContext context) {
        this.layout = layout;
        this.context = context;

        this.layout.setApplicationContext(context);
        this.layout.setNavigator(this);
    }

    public void goTo(String viewName) {
        goTo(viewName, null);
    }

    public void goTo(String viewName, Consumer<Object> controllerCallback) {
        try {
            Node view = load(viewName, controllerCallback);
            layout.setContent(view);
            layout.updateTopbar();
        } catch (IOException e) {
            throw new RuntimeException("Navigation failed for view: " + viewName, e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error during navigation to: " + viewName, e);
        }
    }

    private Node load(String name, Consumer<Object> controllerCallback) throws IOException {
        FXMLLoader loader = ViewLoader.load(name);
        Node node = loader.load();

        Object controller = loader.getController();
        if (controller instanceof BaseController baseController) {
            baseController.setApplicationContext(context);
            baseController.setNavigator(this);
        }

        if (controllerCallback != null) {
            controllerCallback.accept(controller);
        }

        return node;
    }

    public void notify(String message) {
        layout.showNotification(message);
    }
}