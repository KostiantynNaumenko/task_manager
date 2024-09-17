package com.example.taskmanager.services.impl;

import com.example.taskmanager.dtos.task.TaskDto;
import com.example.taskmanager.dtos.task.TaskMappingUtils;
import com.example.taskmanager.enteties.Task;
import com.example.taskmanager.enteties.enums.Status;
import com.example.taskmanager.repositories.TaskRepository;
import com.example.taskmanager.services.TaskService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Getter
@Service
public class TaskServiceImpl implements TaskService {

    private static final String MAX_NUMBER_OF_TASK = "task.maximum.number.in.one.status";

    private final TaskMappingUtils taskMappingUtils;
    private final TaskRepository taskRepository;
    private final Environment environment;

    public TaskServiceImpl(TaskMappingUtils taskMappingUtils, TaskRepository taskRepository, Environment environment) {
        this.taskMappingUtils = taskMappingUtils;
        this.taskRepository = taskRepository;
        this.environment = environment;
    }

    @Override
    public List<TaskDto> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(taskMappingUtils::mapToTaskDto)
                .toList();
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        taskRepository.deleteById(id);
    }

    @Override
    public Long createTask(TaskDto task) {
        if (taskRepository.existsTasksByTitle(task.getTitle())) {
            throw new EntityExistsException("Task already exists");
        }
        int numberOfTasksInTODO = taskRepository.countTasksByStatus(Status.TODO);
        if (numberOfTasksInTODO > Integer.parseInt(environment.getProperty(MAX_NUMBER_OF_TASK)))
        {
            throw new UnsupportedOperationException("Too many tasks in TODO status");
        }
        task.setCreated(getCurrentDate());
        task.setUpdated(getCurrentDate());
        return taskRepository.save(taskMappingUtils.mapToTaskEntity(task)).getId();
    }

    @Override
    public TaskDto updateTaskStatus(Long id, Status status) {
        Task task = getTaskById(id);
        int numberOfTasksInUpdatingStatus = taskRepository.countTasksByStatus(status);
        if (numberOfTasksInUpdatingStatus > Integer.parseInt(environment.getProperty(MAX_NUMBER_OF_TASK)))
        {
            throw new UnsupportedOperationException(String.format("Too many tasks in %s status", status.toString()));
        }
        task.setStatus(status);
        task.setUpdated(getCurrentDate());
        return taskMappingUtils.mapToTaskDto(taskRepository.save(task));
    }

    @Override
    public TaskDto updateTaskFields(Long id, String description, String title) {
        Task task = getTaskById(id);
        task.setDescription(description);
        task.setTitle(title);
        task.setUpdated(getCurrentDate());
        return taskMappingUtils.mapToTaskDto(taskRepository.save(task));
    }

    private Task getTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    private Date getCurrentDate() {
        return new Date(System.currentTimeMillis());
    }
}
