package Class;

import java.util.ArrayList;

public class Task {
	private String id, title, description;
	private ArrayList<Engineer> engineer_assigned;
	private PriorityLevel priority;
	private TaskStatus status;
	private TaskCategory category;
	private NotificationType notification;
	private ArrayList<Task> tasksDependecies;
	private ArrayList<TaskHistoryEntry> history;
	
	public Task(String id, String title, String description, ArrayList<Engineer> engineer_assigned,
			PriorityLevel priority, TaskStatus status, TaskCategory category, NotificationType notification,
			ArrayList<Task> tasksDependecies, ArrayList<TaskHistoryEntry> history) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.engineer_assigned = engineer_assigned;
		this.priority = priority;
		this.status = status;
		this.category = category;
		this.notification = notification;
		this.tasksDependecies = tasksDependecies;
		this.history = history;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ArrayList<Engineer> getEngineer_assigned() {
		return engineer_assigned;
	}

	public void setEngineer_assigned(ArrayList<Engineer> engineer_assigned) {
		this.engineer_assigned = engineer_assigned;
	}

	public PriorityLevel getPriority() {
		return priority;
	}

	public void setPriority(PriorityLevel priority) {
		this.priority = priority;
	}

	public TaskStatus getStatus() {
		return status;
	}

	public void setStatus(TaskStatus status) {
		this.status = status;
	}

	public TaskCategory getCategory() {
		return category;
	}

	public void setCategory(TaskCategory category) {
		this.category = category;
	}

	public NotificationType getNotification() {
		return notification;
	}

	public void setNotification(NotificationType notification) {
		this.notification = notification;
	}

	public ArrayList<Task> getTasksDependecies() {
		return tasksDependecies;
	}

	public void setTasksDependecies(ArrayList<Task> tasksDependecies) {
		this.tasksDependecies = tasksDependecies;
	}

	public ArrayList<TaskHistoryEntry> getHistory() {
		return history;
	}

	public void setHistory(ArrayList<TaskHistoryEntry> history) {
		this.history = history;
	}
	
	public void updateStatus(TaskStatus newStatus) {
		this.status = newStatus;
	}
	
	public void addHistory(TaskHistoryEntry newEntry) {
		this.history.add(newEntry);
	}
}
