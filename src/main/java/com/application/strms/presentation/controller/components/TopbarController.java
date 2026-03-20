package com.application.strms.presentation.controller.components;

import com.application.strms.presentation.controller.BaseController;
import com.application.strms.presentation.service.UiConstants;

public abstract class TopbarController extends BaseController {
    protected void navigateHome() {
        navigator.goTo(UiConstants.Pages.HOME);
    }

    protected void logout() {
        context.getSessionManager().logout();
        navigator.goTo(UiConstants.Pages.LOGIN);
    }
}
