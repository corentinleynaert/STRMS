package com.application.strms.domain.model;
import java.security.Timestamp;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Comparator;

public class TaskManager {
    private HashMap<String, Task> Tasks = new HashMap<>();
    private HashMap<String, Users> Tasks = new HashMap<>();
    PriorityQueue<Task> waitingQueue = new PriorityQueue<>(
            Comparator.comparingInt((Task t) -> t.priority.getValue()).reversed()
        );
    
    public void createTask(String title, String description, TaskCategory category, PriorityLevel priority, ArrayList<Users> assignedUser) {
        Ulid taskId = new Ulid();
        Task newTask = new Task(taskId, title, description, assignedUser, priority, TaskStatus.TO_DO, category, NotificationType.NONE, new ArrayList<>(), new ArrayList<>());
        Tasks.put(newTask.getId(), newTask);
    }
    public void deleteTask(String id) {
        Tasks.remove(id);
    }
    public void updateTask(String id, String title, String description, TaskCategory category, PriorityLevel priority, ArrayList<Users> assignedUser) {
        Task taskToUpdate = Tasks.get(id);
        if (taskToUpdate != null) {
            taskToUpdate.setTitle(title);
            taskToUpdate.setDescription(description);
            taskToUpdate.setCategory(category);
            taskToUpdate.setPriority(priority);
            taskToUpdate.setAssignedUser(assignedUser);
        }
    }
    public void updateStatus(String id, TaskStatus newStatus) {
        User currentUser = sessionManager.getCurrentUser();
        
        Task taskToUpdate = Tasks.get(id);
        if (taskToUpdate != null 
            && currentUser.isEngineer() 
            && taskToUpdate.getAssignedUser().contains(currentUser) 
            && taskToUpdate.getTasksDependecies().stream().noneMatch(task -> task.getStatus() != TaskStatus.DONE)
            ) {
            taskToUpdate.setStatus(newStatus);
        }
        else {
            throw new IllegalArgumentException("Only assigned engineers can update the task status.");
        }
    }
    public assignTask(String taskId, String userId) {
        User currentUser = sessionManager.getCurrentUser();
        Task taskToAssign = Tasks.get(taskId);
        User userToAssign = Users.get(userId);
        if (taskToAssign != null && currentUser.isAdmin() &&userToAssign != null && userToAssign.isEngineer()) {
            taskToAssign.getAssignedUser().add(userToAssign);
        }
    }
    public Task readTask(String id) {
        return this.Tasks.get(id);
    }
    public addDependency(String taskId, String dependencyId) {
        Task task = Tasks.get(taskId);
        Task dependency = Tasks.get(dependencyId);
        if (task != null && dependency != null ) {// il faut verifier aussi la dependance circulaire (pas implementé)
            task.getTasksDependecies().add(dependency);
        }
    }// imlementer detectCircularDependency() jsp comment on fait

}
