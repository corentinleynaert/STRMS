package com.application.strms.presentation.model;

import com.application.strms.domain.model.Task;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TaskDisplay {
    private final Task task;
    private final StringProperty title;
    private final StringProperty priority;
    private final StringProperty status;

    public TaskDisplay(Task task) {
        this.task = task;
        this.title = new SimpleStringProperty(task.getTitle());
        this.priority = new SimpleStringProperty(task.getPriority().name());
        this.status = new SimpleStringProperty(task.getStatus().name());
    }

    public Task getTask() {
        return task;
    }

    public StringProperty titleProperty() {
        return title;
    }

    public StringProperty priorityProperty() {
        return priority;
    }

    public StringProperty statusProperty() {
        return status;
    }
}