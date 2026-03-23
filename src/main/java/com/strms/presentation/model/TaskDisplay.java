package com.strms.presentation.model;

import com.strms.domain.model.Task;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TaskDisplay {
    private final Task task;
    private final StringProperty title;
    private final StringProperty priority;
    private final StringProperty status;
    private final StringProperty category;
    private final StringProperty deadline;

    public TaskDisplay(Task task) {
        this.task = task;
        this.title = new SimpleStringProperty(task.getTitle());
        this.priority = new SimpleStringProperty(task.getPriority().name());
        this.status = new SimpleStringProperty(task.getStatus().name());
        this.category = new SimpleStringProperty(task.getCategory().name());
        this.deadline = new SimpleStringProperty(
                task.getDeadline() != null ? task.getDeadline().toString() : "No deadline");
    }

    public Task getTask() {
        return task;
    }

    public String getTitle() {
        return title.get();
    }

    public String getPriority() {
        return priority.get();
    }

    public String getStatus() {
        return status.get();
    }

    public String getCategory() {
        return category.get();
    }

    public String getDeadline() {
        return deadline.get();
    }
}