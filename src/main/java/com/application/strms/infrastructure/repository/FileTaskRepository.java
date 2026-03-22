package com.application.strms.infrastructure.repository;

import com.application.strms.domain.exception.DuplicateTaskException;
import com.application.strms.domain.exception.FilePersistenceException;
import com.application.strms.domain.exception.TaskNotFoundException;
import com.application.strms.domain.model.Engineer;
import com.application.strms.domain.model.PriorityLevel;
import com.application.strms.domain.model.Task;
import com.application.strms.domain.model.TaskCategory;
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
            restoreAssignedEngineers();
        } catch (IOException e) {
            throw new FilePersistenceException("Failed to initialize task repository from file", e);
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
            throw new FilePersistenceException("Failed to save task: " + task.getUlid(), e);
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
            throw new FilePersistenceException("Failed to update task: " + task.getUlid(), e);
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
            throw new FilePersistenceException("Failed to delete task: " + id, e);
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

            return new Task(ulid, title, description, priority, category, status, deadline);
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

    private void restoreAssignedEngineers() {
        for (Map.Entry<Ulid, Ulid> entry : assignedEngineerMap.entrySet()) {
            Task task = tasks.get(entry.getKey());
            if (task != null) {
                try {
                    List<User> users = userRepository.getAllUsers();
                    for (User user : users) {
                        if (user instanceof Engineer engineer && engineer.getId().equals(entry.getValue())) {
                            task.assignEngineerUnchecked(engineer);
                            break;
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Failed to restore assigned engineer for task: " + entry.getKey());
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

        return sb.toString();
    }
}
