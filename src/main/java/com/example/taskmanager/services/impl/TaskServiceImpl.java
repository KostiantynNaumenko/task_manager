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
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Getter
@Service
public class TaskServiceImpl implements TaskService {

    private final TaskMappingUtils taskMappingUtils;
    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskMappingUtils taskMappingUtils, TaskRepository taskRepository) {
        this.taskMappingUtils = taskMappingUtils;
        this.taskRepository = taskRepository;
    }

    @Override
    public List<TaskDto> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(taskMappingUtils::mapToTaskDto)
                .toList();
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    @Override
    public Long createTask(TaskDto task) {
        if (task.getId() != null) {
            throw new EntityExistsException("Task already exists");
        }
        task.setCreated(getCurrentDate());
        task.setUpdated(getCurrentDate());
        return taskRepository.save(taskMappingUtils.mapToTaskEntity(task)).getId();
    }

    @Override
    public TaskDto updateTaskStatus(Long id, Status status) {
        Task task = getTaskById(id);
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
