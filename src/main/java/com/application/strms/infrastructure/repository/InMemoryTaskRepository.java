package com.application.strms.infrastructure.repository;

import com.application.strms.domain.exception.DuplicateTaskException;
import com.application.strms.domain.exception.TaskNotFoundException;
import com.application.strms.domain.model.Task;
import com.application.strms.domain.model.Ulid;
import com.application.strms.domain.repository.TaskRepository;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskRepository implements TaskRepository {
    private final Map<Ulid, Task> tasks = new HashMap<>();

    @Override
    public void save(Task task) throws IOException {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }

        if (tasks.containsKey(task.getUlid())) {
            throw new DuplicateTaskException("Task already exists: " + task.getUlid());
        }

        tasks.put(task.getUlid(), task);
    }

    @Override
    public void update(Task task) throws IOException {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }

        if (!tasks.containsKey(task.getUlid())) {
            throw new TaskNotFoundException("Task not found: " + task.getUlid());
        }

        tasks.put(task.getUlid(), task);
    }

    @Override
    public void delete(Ulid id) throws IOException {
        if (id == null) {
            throw new IllegalArgumentException("Task ID cannot be null");
        }

        if (!tasks.containsKey(id)) {
            throw new TaskNotFoundException("Task not found: " + id);
        }

        tasks.remove(id);
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
}
