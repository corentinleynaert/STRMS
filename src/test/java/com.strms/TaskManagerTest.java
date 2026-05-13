package com.strms;

import com.strms.application.result.CreateTaskResult;
import com.strms.application.result.UpdateTaskResult;
import com.strms.application.service.NotificationService;
import com.strms.application.service.TaskManager;
import com.strms.domain.model.*;
import com.strms.domain.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskManagerTest {

    @Mock
    private TaskRepository repository;

    @Mock
    private NotificationService notificationService;

    private TaskManager taskManager;
    private User adminUser;

    @BeforeEach
    void setUp() throws IOException {

        adminUser = mock(User.class);
        UserRole adminRole = mock(UserRole.class);

        when(adminRole.canCreateTask()).thenReturn(true);
        when(adminRole.canDeleteTask()).thenReturn(true);
        when(adminUser.getRole()).thenReturn(adminRole);

        when(repository.findAll()).thenReturn(new ArrayList<>());

        doNothing().when(repository).save(any(Task.class));

        taskManager = new TaskManager(repository, notificationService);
    }

    @Test
    @DisplayName("Test 1 : Création et dépendance avec FEATURE")
    void shouldAddValidDependencies() throws IOException {

        Task parent = taskManager
                .createTask(
                        "Parent",
                        "Desc",
                        PriorityLevel.HIGH,
                        TaskCategory.FEATURE,
                        null,
                        adminUser
                )
                .getTask();

        Task child = taskManager
                .createTask(
                        "Child",
                        "Desc",
                        PriorityLevel.MEDIUM,
                        TaskCategory.FEATURE,
                        null,
                        adminUser
                )
                .getTask();

        UpdateTaskResult result =
                taskManager.addDependency(
                        child.getUlid(),
                        parent.getUlid(),
                        adminUser
                );

        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("Test 2 : Rejet de cycle avec BUGFIX")
    void shouldRejectSelfCircularDependency() throws IOException {

        Task taskA = taskManager
                .createTask(
                        "Task A",
                        "Desc",
                        PriorityLevel.HIGH,
                        TaskCategory.BUGFIX,
                        null,
                        adminUser
                )
                .getTask();

        UpdateTaskResult result =
                taskManager.addDependency(
                        taskA.getUlid(),
                        taskA.getUlid(),
                        adminUser
                );

        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("Test 3 : Permissions avec DOCUMENTATION")
    void shouldValidateUserRolePermissions() throws IOException {

        User guest = mock(User.class);
        UserRole guestRole = mock(UserRole.class);

        when(guestRole.canCreateTask()).thenReturn(false);
        when(guest.getRole()).thenReturn(guestRole);

        CreateTaskResult result =
                taskManager.createTask(
                        "Doc Task",
                        "D",
                        PriorityLevel.LOW,
                        TaskCategory.DOCUMENTATION,
                        null,
                        guest
                );

        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("Test 4 : Recherche avec RESEARCH")
    void shouldCreateResearchTask() throws IOException {

        CreateTaskResult result =
                taskManager.createTask(
                        "Deep Dive",
                        "Research",
                        PriorityLevel.MEDIUM,
                        TaskCategory.RESEARCH,
                        null,
                        adminUser
                );

        assertTrue(result.isSuccess());
        assertNotNull(result.getTask());
    }
}