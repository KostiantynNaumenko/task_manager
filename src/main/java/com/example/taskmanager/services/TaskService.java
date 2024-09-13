package com.example.taskmanager.services;

import com.example.taskmanager.dtos.task.TaskDto;
import com.example.taskmanager.enteties.enums.Status;

import java.util.List;

public interface TaskService {

    List<TaskDto> getAllTasks();

    void deleteTask(Long id);

    Long createTask(TaskDto task);

    TaskDto updateTaskStatus(Long id, Status status);

    TaskDto updateTaskFields(Long id, String title, String description);
}
