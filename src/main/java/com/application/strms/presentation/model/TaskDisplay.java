package com.application.strms.presentation.model;

import com.application.strms.domain.model.Task;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TaskDisplay {
    private final Task task;
    private final StringProperty title;
    private final StringProperty description;
    private final StringProperty priority;
    private final StringProperty status;
    private final StringProperty category;
    private final StringProperty assignedEngineer;
    private final StringProperty deadline;

    public TaskDisplay(Task task) {
        this.task = task;
        this.title = new SimpleStringProperty(task.getTitle());
        this.description = new SimpleStringProperty(task.getDescription());
        this.priority = new SimpleStringProperty(task.getPriority().name());
        this.status = new SimpleStringProperty(task.getStatus().name());
        this.category = new SimpleStringProperty(task.getCategory().name());
        this.assignedEngineer = new SimpleStringProperty(
                task.getAssignedEngineer() != null ? task.getAssignedEngineer().getName() : "Unassigned");
        this.deadline = new SimpleStringProperty(
                task.getDeadline() != null ? task.getDeadline().toString() : "No deadline");
    }

    public Task getTask() {
        return task;
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public String getPriority() {
        return priority.get();
    }

    public StringProperty priorityProperty() {
        return priority;
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public String getCategory() {
        return category.get();
    }

    public StringProperty categoryProperty() {
        return category;
    }

    public String getAssignedEngineer() {
        return assignedEngineer.get();
    }

    public StringProperty assignedEngineerProperty() {
        return assignedEngineer;
    }

    public String getDeadline() {
        return deadline.get();
    }

    public StringProperty deadlineProperty() {
        return deadline;
    }
}