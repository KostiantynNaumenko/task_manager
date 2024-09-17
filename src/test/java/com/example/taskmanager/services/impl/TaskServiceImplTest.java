package com.example.taskmanager.services.impl;

import com.example.taskmanager.dtos.task.TaskDto;
import com.example.taskmanager.dtos.task.TaskMappingUtils;
import com.example.taskmanager.enteties.Task;
import com.example.taskmanager.enteties.enums.Status;
import com.example.taskmanager.repositories.TaskRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {
    private static TaskServiceImpl taskServiceImpl;

    @Mock
    private TaskRepository taskRepositoryMock;

    @Mock
    private TaskMappingUtils taskMappingUtilsMock;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(TaskServiceImplTest.class);
        assertNotNull(taskRepositoryMock);
        assertNotNull(taskMappingUtilsMock);
        taskServiceImpl = new TaskServiceImpl(taskMappingUtilsMock, taskRepositoryMock);
        assertNotNull(taskServiceImpl);

    }

    @Test
    public void getAllTasks_ValidListOfTasks() {
        when(taskRepositoryMock.findAll()).thenReturn(getListOfTasks());
        List<Task> testTasks = taskRepositoryMock.findAll();
        when(taskMappingUtilsMock.mapToTaskDto(testTasks.get(0))).thenReturn(getTaskDto());
        List<TaskDto> taskDtos = taskServiceImpl.getAllTasks();

        assertNotNull(taskDtos);
        assertEquals(testTasks.size(), taskDtos.size());
    }

    @Test
    public void deleteTask_ValidTask() {
        when(taskRepositoryMock.findById(1L)).thenReturn(Optional.of(new Task()));
        taskServiceImpl.deleteTask(1L);

        verify(taskRepositoryMock, times(1)).findById(1L);
        verify(taskRepositoryMock, times(1)).deleteById(1L);
    }

    @Test
    public void deleteTask_InvalidTask() {
        when(taskRepositoryMock.findById(1L)).thenReturn(Optional.empty());
        try
        {
            taskServiceImpl.deleteTask(1L);
            assert false;
        } catch (EntityNotFoundException e) {
            verify(taskRepositoryMock, times(1)).findById(1L);
            verify(taskRepositoryMock, never()).deleteById(1L);
        }
    }

    @Test
    public void createTask_ValidTask() {
        TaskDto taskDto = new TaskDto("New Task Title", "New Task Description");
        when(taskMappingUtilsMock.mapToTaskEntity(taskDto))
                .thenReturn(new Task("New Task Title", "New Task Description"));
        when(taskRepositoryMock.save(any(Task.class)))
                .thenReturn(new Task(1L, "New Task Title", "New Task Description"));
        Long taskId = taskServiceImpl.createTask(taskDto);
        verify(taskRepositoryMock, times(1)).save(any(Task.class));
        assertEquals(taskId, 1L);
    }

    @Test
    public void createTask_InvalidTask() {
        TaskDto taskDto = new TaskDto(1L, "New Task Title", "New Task Description", null, null, null);
        try {
            taskServiceImpl.createTask(taskDto);
            assert false;
        } catch (EntityExistsException e) {
            verify(taskRepositoryMock, never()).save(any(Task.class));
        }
    }

    @Test
    public void updateTaskStatus_ValidTask() {
        when(taskRepositoryMock.findById(1L)).thenReturn(Optional.of(new Task()));
        when(taskRepositoryMock.save(any(Task.class))).thenReturn(new Task());
        taskServiceImpl.updateTaskStatus(1L, Status.IN_PROGRESS);
        verify(taskRepositoryMock, times(1)).findById(1L);
        verify(taskRepositoryMock, times(1)).save(any(Task.class));
    }

    @Test
    public void updateTaskStatus_InvalidTask() {
        when(taskRepositoryMock.findById(1L)).thenReturn(Optional.empty());
        try {
            taskServiceImpl.updateTaskStatus(1L, Status.IN_PROGRESS);
            assert false;
        } catch (EntityNotFoundException e) {
            verify(taskRepositoryMock, times(1)).findById(1L);
            verify(taskRepositoryMock, never()).save(any(Task.class));
        }
    }

    @Test
    public void updateTaskFields_ValidTask() {
        when(taskRepositoryMock.findById(1L)).thenReturn(Optional.of(new Task()));
        when(taskRepositoryMock.save(any(Task.class))).thenReturn(new Task());
        taskServiceImpl.updateTaskFields(1L, "new title", "new description");
        verify(taskRepositoryMock, times(1)).findById(1L);
        verify(taskRepositoryMock, times(1)).save(any(Task.class));
    }

    @Test
    public void updateTaskSFields_InvalidTask() {
        when(taskRepositoryMock.findById(1L)).thenReturn(Optional.empty());
        try {
            taskServiceImpl.updateTaskFields(1L, "new title", "new description");
            assert false;
        } catch (EntityNotFoundException e) {
            verify(taskRepositoryMock, times(1)).findById(1L);
            verify(taskRepositoryMock, never()).save(any(Task.class));
        }
    }

    private TaskDto getTaskDto() {
        TaskDto task = new TaskDto();
        task.setId(1L);
        task.setTitle("Task Title");
        task.setDescription("Task Description");
        task.setCreated(getCurrentDate());
        task.setUpdated(getCurrentDate());
        task.setStatus(Status.TODO);
        return task;
    }

    private List<Task> getListOfTasks() {
        List<Task> tasks = new ArrayList<>();
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Task Title");
        task.setDescription("Task Description");
        task.setCreated(getCurrentDate());
        task.setUpdated(getCurrentDate());
        task.setStatus(Status.TODO);
        tasks.add(task);
        return tasks;
    }

    private Date getCurrentDate() {
        return new Date(System.currentTimeMillis());
    }
}