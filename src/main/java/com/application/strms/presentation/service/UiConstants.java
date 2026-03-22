package com.application.strms.presentation.service;

public final class UiConstants {

    private UiConstants() {
        throw new AssertionError("Utility class");
    }

    public static final class Pages {
        public static final String HOME = "Home";
        public static final String LOGIN = "Login";
        public static final String ADD_USER = "AddUser";
        public static final String UPDATE_USER = "UpdateUser";
        public static final String USERS = "Users";
        public static final String TASKS = "Tasks";
        public static final String ADD_TASK = "AddTask";
        public static final String UPDATE_TASK = "UpdateTask";
        public static final String ENGINEER_TASKS = "EngineerTasks";
        public static final String ENGINEER_TASK_DETAILS = "EngineerTaskDetails";
        public static final String ANALYTICS_REPORT = "AnalyticsReport";

        private Pages() {
            throw new AssertionError("Utility class");
        }
    }

    public static final class Roles {
        public static final String ADMIN = "ADMIN";
        public static final String MANAGER = "MANAGER";
        public static final String ENGINEER = "ENGINEER";

        private Roles() {
            throw new AssertionError("Utility class");
        }
    }

    public static final class Messages {
        public static final String INVALID_CREDENTIALS = "Email or password is incorrect";
        public static final String USER_CREATED = "User created successfully!";
        public static final String USER_UPDATED = "User updated successfully!";
        public static final String ERROR_ACCESSING_DATA = "Error accessing user data: ";
        public static final String NO_USER_SELECTED = "No user selected for update";
        public static final String TASK_CREATED = "Task created successfully!";
        public static final String TASK_UPDATED = "Task updated successfully!";
        public static final String TASK_DEPENDENCY_ADDED = "Dependency added successfully!";
        public static final String TASK_DEPENDENCY_REMOVED = "Dependency removed successfully!";
        public static final String ERROR_ACCESSING_TASK_DATA = "Error accessing task data: ";
        public static final String NO_TASK_SELECTED = "No task selected for update";

        private Messages() {
            throw new AssertionError("Utility class");
        }
    }

    public static final class Styles {
        public static final String NOTIFICATION = "notification";

        private Styles() {
            throw new AssertionError("Utility class");
        }
    }

    public static final class Animations {
        public static final int SHAKE_DURATION_MS = 50;
        public static final int SHAKE_DISTANCE = 10;
        public static final int SHAKE_CYCLES = 4;
        public static final int FADE_TRANSITION_MS = 200;
        public static final int PAUSE_BEFORE_REMOVE_MS = 2500;
        public static final double BUTTON_HOVERED_OPACITY = 0.9;
        public static final double BUTTON_NORMAL_OPACITY = 1.0;

        private Animations() {
            throw new AssertionError("Utility class");
        }
    }
}
