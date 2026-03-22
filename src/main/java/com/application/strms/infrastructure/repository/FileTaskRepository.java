package com.application.strms.infrastructure.repository;

import com.application.strms.domain.exception.DuplicateTaskException;
import com.application.strms.domain.exception.FilePersistenceException;
import com.application.strms.domain.exception.TaskNotFoundException;
import com.application.strms.domain.model.Admin;
import com.application.strms.domain.model.Email;
import com.application.strms.domain.model.Engineer;
import com.application.strms.domain.model.Manager;
import com.application.strms.domain.model.PriorityLevel;
import com.application.strms.domain.model.Task;
import com.application.strms.domain.model.TaskCategory;
import com.application.strms.domain.model.TaskHistoryEntry;
import com.application.strms.domain.model.TaskStatus;
import com.application.strms.domain.model.Ulid;
import com.application.strms.domain.model.User;
import com.application.strms.domain.repository.TaskRepository;
import com.application.strms.domain.repository.UserRepository;
import com.application.strms.infrastructure.persistence.FileHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileTaskRepository implements TaskRepository {
    private static final String TASKS_FILE = "tasks.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final Map<Ulid, Task> tasks = new HashMap<>();
    private final FileHandler fileHandler;
    private final UserRepository userRepository;
    private final Map<Ulid, List<Ulid>> dependencyMap = new HashMap<>();
    private final Map<Ulid, Ulid> assignedEngineerMap = new HashMap<>();

    public FileTaskRepository(FileHandler fileHandler, UserRepository userRepository) throws IOException {
        this.fileHandler = fileHandler;
        this.userRepository = userRepository;

        try {
            List<Task> loadedTasks = fileHandler.load(TASKS_FILE, this::mapLineToTask);

            for (Task task : loadedTasks) {
                tasks.put(task.getUlid(), task);
            }

            reconstituteDependencies();
            try {
                restoreAssignedEngineers();
            } catch (IOException e) {
                throw new IOException("Failed to restore assigned engineers", e);
            }
        } catch (IOException e) {
            throw new IOException("Failed to initialize task repository from file", e);
        }
    }

    @Override
    public void save(Task task) throws IOException {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }

        if (tasks.containsKey(task.getUlid())) {
            throw new DuplicateTaskException("Task already exists: " + task.getUlid());
        }

        try {
            tasks.put(task.getUlid(), task);
            fileHandler.save(TASKS_FILE, List.of(task), this::mapTaskToString);
        } catch (IOException e) {
            throw new IOException("Failed to save task: " + task.getUlid(), e);
        }
    }

    @Override
    public void update(Task task) throws IOException {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }

        if (!tasks.containsKey(task.getUlid())) {
            throw new TaskNotFoundException("Task not found: " + task.getUlid());
        }

        try {
            tasks.put(task.getUlid(), task);

            List<Task> allTasks = new ArrayList<>(tasks.values());
            fileHandler.replaceAll(TASKS_FILE, allTasks, this::mapTaskToString);
        } catch (IOException e) {
            throw new IOException("Failed to update task: " + task.getUlid(), e);
        }
    }

    @Override
    public void delete(Ulid id) throws IOException {
        if (id == null) {
            throw new IllegalArgumentException("Task ID cannot be null");
        }

        if (!tasks.containsKey(id)) {
            throw new TaskNotFoundException("Task not found: " + id);
        }

        try {
            tasks.remove(id);

            List<Task> allTasks = new ArrayList<>(tasks.values());
            fileHandler.replaceAll(TASKS_FILE, allTasks, this::mapTaskToString);
        } catch (IOException e) {
            throw new IOException("Failed to delete task: " + id, e);
        }
    }

    @Override
    public Task findById(Ulid id) {
        if (id == null) {
            throw new IllegalArgumentException("Task ID cannot be null");
        }

        return tasks.get(id);
    }

    @Override
    public List<Task> findAll() {
        return tasks.values().stream().toList();
    }

    @Override
    public boolean exists(Ulid id) {
        if (id == null) {
            throw new IllegalArgumentException("Task ID cannot be null");
        }

        return tasks.containsKey(id);
    }

    private Task mapLineToTask(String line) {
        try {
            String[] parts = line.split(";");

            if (parts.length < 9) {
                throw new IllegalArgumentException("Invalid task line format: " + line);
            }

            Ulid ulid = Ulid.fromString(parts[0]);
            String title = parts[1];
            String description = parts[2];
            PriorityLevel priority = PriorityLevel.valueOf(parts[3]);
            TaskCategory category = TaskCategory.valueOf(parts[4]);

            String deadlineStr = parts[5];
            LocalDateTime deadline = deadlineStr.equals("null") ? null
                    : LocalDateTime.parse(deadlineStr, DATE_FORMATTER);

            String engineerIdStr = parts[6];
            if (!engineerIdStr.equals("null")) {
                assignedEngineerMap.put(ulid, Ulid.fromString(engineerIdStr));
            }

            TaskStatus status = TaskStatus.valueOf(parts[7]);

            String dependenciesStr = parts[8];
            List<Ulid> depUlids = new ArrayList<>();
            if (!dependenciesStr.equals("null") && !dependenciesStr.isEmpty()) {
                String[] depIds = dependenciesStr.split("\\|");
                for (String depId : depIds) {
                    if (!depId.isBlank()) {
                        depUlids.add(Ulid.fromString(depId));
                    }
                }
            }
            dependencyMap.put(ulid, depUlids);

            Task task = new Task(ulid, title, description, priority, category, status, deadline);

            if (parts.length >= 10 && !parts[9].equals("null") && !parts[9].isEmpty()) {
                loadHistoryIntoTask(task, parts[9]);
            }

            return task;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse task line: " + line, e);
        }
    }

    private void reconstituteDependencies() {
        for (Map.Entry<Ulid, List<Ulid>> entry : dependencyMap.entrySet()) {
            Task task = tasks.get(entry.getKey());
            if (task != null) {
                for (Ulid depUlid : entry.getValue()) {
                    Task dep = tasks.get(depUlid);
                    if (dep != null) {
                        task.addDependencyUnchecked(dep);
                    }
                }
                task.refreshStatusFromDependencies();
            }
        }
    }

    private void restoreAssignedEngineers() throws IOException {
        for (Map.Entry<Ulid, Ulid> entry : assignedEngineerMap.entrySet()) {
            Task task = tasks.get(entry.getKey());
            if (task != null) {
                List<User> users = userRepository.getAllUsers();
                for (User user : users) {
                    if (user instanceof Engineer engineer && engineer.getId().equals(entry.getValue())) {
                        task.assignEngineerUnchecked(engineer);
                        break;
                    }
                }
            }
        }
    }

    private String mapTaskToString(Task task) {
        StringBuilder sb = new StringBuilder();

        sb.append(task.getUlid()).append(";");
        sb.append(task.getTitle()).append(";");
        sb.append(task.getDescription()).append(";");
        sb.append(task.getPriority()).append(";");
        sb.append(task.getCategory()).append(";");

        LocalDateTime deadline = task.getDeadline();
        sb.append(deadline == null ? "null" : deadline.format(DATE_FORMATTER)).append(";");

        if (task.getAssignedEngineer() != null) {
            sb.append(task.getAssignedEngineer().getId());
        } else {
            sb.append("null");
        }
        sb.append(";");

        sb.append(task.getStatus()).append(";");

        List<Task> dependencies = task.getDependencies();
        if (dependencies.isEmpty()) {
            sb.append("null");
        } else {
            String deps = dependencies.stream()
                    .map(t -> t.getUlid().toString())
                    .reduce((a, b) -> a + "|" + b)
                    .orElse("null");
            sb.append(deps);
        }
        sb.append(";");

        List<TaskHistoryEntry> history = task.getHistory();
        if (history.isEmpty()) {
            sb.append("null");
        } else {
            String historyStr = serializeHistory(history);
            sb.append(historyStr);
        }

        return sb.toString();
    }

    private String serializeHistory(List<TaskHistoryEntry> history) {
        List<String> parts = new ArrayList<>();
        for (TaskHistoryEntry entry : history) {
            String timestamp = entry.getTimestamp().format(DATE_FORMATTER);
            String action = entry.getAction().replace("|||", "___").replace("$", "_DOLLAR_");
            String userId = entry.getPerformedBy().getId().toString();
            String userName = entry.getPerformedBy().getName().replace("|||", "___").replace("$", "_DOLLAR_");
            String userRole = entry.getPerformedBy().getRole().getIdentifier();

            String entryStr = String.format("%s|||%s|||%s|||%s|||%s", timestamp, action, userId, userName, userRole);
            parts.add(entryStr);
        }
        return String.join("$", parts);
    }

    private void loadHistoryIntoTask(Task task, String historyStr) {
        if (historyStr == null || historyStr.isEmpty() || historyStr.equals("null")) {
            return;
        }

        String[] entries = historyStr.split("\\$");
        for (String entry : entries) {
            if (entry.isBlank())
                continue;

            String[] fields = entry.split("\\|\\|\\|");
            if (fields.length >= 5) {
                try {
                    String timestamp = fields[0];
                    String action = fields[1].replace("_DOLLAR_", "$").replace("___", "|||");
                    String userId = fields[2];
                    String userName = fields[3].replace("_DOLLAR_", "$").replace("___", "|||");
                    String userRole = fields[4];

                    LocalDateTime dateTime = LocalDateTime.parse(timestamp, DATE_FORMATTER);

                    User performedBy = createUserFromData(userId, userName, userRole);

                    TaskHistoryEntry historyEntry = new TaskHistoryEntry(action, performedBy, dateTime);
                    task.addHistoryEntry(historyEntry);
                } catch (Exception e) {
                    throw new FilePersistenceException("Failed to load task history", e);
                }
            }
        }
    }

    private User createUserFromData(String userId, String userName, String userRole) {
        List<User> allUsers = userRepository.getAllUsers();
        for (User user : allUsers) {
            if (user.getId().toString().equals(userId)) {
                return user;
            }
        }

        return createUserAsTemporarySubstitute(userId, userName, userRole);
    }

    private User createUserAsTemporarySubstitute(String userId, String userName, String userRole) {
        try {
            Ulid ulid = Ulid.fromString(userId);
            Email email = new Email(userName.toLowerCase().replace(" ", ".") + "@app.local");

            return switch (userRole) {
                case "ADMIN" -> new Admin(ulid, userName, email);
                case "MANAGER" -> new Manager(ulid, userName, email);
                case "ENGINEER" -> new Engineer(ulid, userName, email);
                default -> new Engineer(ulid, userName, email);
            };
        } catch (IllegalArgumentException e) {
            throw new FilePersistenceException("Failed to create temporary user with ID: " + userId, e);
        }
    }
}
