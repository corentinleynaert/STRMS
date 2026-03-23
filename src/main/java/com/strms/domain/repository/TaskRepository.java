package com.strms.domain.repository;

import com.strms.domain.model.Task;
import com.strms.domain.model.Ulid;
import java.io.IOException;
import java.util.List;

public interface TaskRepository {
    void save(Task task) throws IOException;

    void update(Task task) throws IOException;

    void delete(Ulid id) throws IOException;

    List<Task> findAll();
}
