package Class;

import java.security.Timestamp;

public class TaskHistoryEntry {
	private String id;
	private String message;
	private Timestamp day_hour;
	
	public TaskHistoryEntry(String id, String message, Timestamp day_hour) {
		super();
		this.id = id;
		this.message = message;
		this.day_hour = day_hour;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Timestamp getDay_hour() {
		return day_hour;
	}

	public void setDay_hour(Timestamp day_hour) {
		this.day_hour = day_hour;
	}
	
	
}
