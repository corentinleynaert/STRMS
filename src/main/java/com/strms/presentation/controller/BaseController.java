package com.strms.presentation.controller;

import com.strms.application.ApplicationContext;
import com.strms.presentation.navigation.Navigator;

public abstract class BaseController {
    protected ApplicationContext context;
    protected Navigator navigator;

    private boolean readyCalled = false;

    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
        this.tryCallReady();
    }

    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
        this.tryCallReady();
    }

    private void tryCallReady() {
        if (!readyCalled && navigator != null && context != null) {
            readyCalled = true;
            onReady();
        }
    }

    protected void onReady() {}
}