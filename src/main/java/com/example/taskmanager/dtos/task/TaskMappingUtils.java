package com.example.taskmanager.dtos.task;

import com.example.taskmanager.enteties.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMappingUtils {

    public Task mapToTaskEntity(TaskDto task) {
        Task taskEntity = new Task();
        taskEntity.setId(task.getId());
        taskEntity.setTitle(task.getTitle());
        taskEntity.setDescription(task.getDescription());
        taskEntity.setStatus(task.getStatus());
        taskEntity.setCreated(task.getCreated());
        taskEntity.setUpdated(task.getUpdated());
        return taskEntity;
    }

    public TaskDto mapToTaskDto(Task task) {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(task.getId());
        taskDto.setTitle(task.getTitle());
        taskDto.setDescription(task.getDescription());
        taskDto.setStatus(task.getStatus());
        taskDto.setUpdated(task.getUpdated());
        taskDto.setCreated(task.getCreated());
        return taskDto;
    }
}
