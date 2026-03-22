package com.application.strms.domain.repository;

import com.application.strms.domain.model.Task;
import com.application.strms.domain.model.Ulid;
import java.io.IOException;
import java.util.List;

public interface TaskRepository {
    void save(Task task) throws IOException;

    void update(Task task) throws IOException;

    void delete(Ulid id) throws IOException;

    Task findById(Ulid id);

    List<Task> findAll();

    boolean exists(Ulid id);
}
