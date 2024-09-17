package com.example.taskmanager.dtos.task;

import com.example.taskmanager.enteties.Task;
import com.example.taskmanager.enteties.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

class TaskMappingUtilsTest {

    private TaskMappingUtils taskMappingUtils;

    private Task task;
    private TaskDto taskDto;

    @BeforeEach
    public void setUp() {
        taskMappingUtils = new TaskMappingUtils();

        task = new Task();
        task.setId(1L);
        task.setTitle("Task Title");
        task.setDescription("Task Description");
        task.setStatus(Status.TODO);
        task.setUpdated(getCurrentDate());
        task.setCreated(getCurrentDate());

        taskDto = new TaskDto();
        taskDto.setId(1L);
        taskDto.setTitle("Task Title");
        taskDto.setDescription("Task Description");
        taskDto.setStatus(Status.TODO);
        taskDto.setUpdated(getCurrentDate());
        taskDto.setCreated(getCurrentDate());
    }

    @Test
    public void mapToTaskEntity() {
        Task entity = taskMappingUtils.mapToTaskEntity(taskDto);
        assertNotNull(entity);
        assertEquals(task.getId(), entity.getId());
        assertEquals(task.getTitle(), entity.getTitle());
        assertEquals(task.getDescription(), entity.getDescription());
        assertEquals(task.getStatus(), entity.getStatus());
        assertEquals(task.getUpdated(), entity.getUpdated());
        assertEquals(task.getCreated(), entity.getCreated());
    }

    @Test
    public void mapToTaskDto() {
        TaskDto dto = taskMappingUtils.mapToTaskDto(task);
        assertNotNull(dto);
        assertEquals(task.getId(), dto.getId());
        assertEquals(task.getTitle(), dto.getTitle());
        assertEquals(task.getDescription(), dto.getDescription());
        assertEquals(task.getStatus(), dto.getStatus());
        assertEquals(task.getUpdated(), dto.getUpdated());
        assertEquals(task.getCreated(), dto.getCreated());
    }

    private Date getCurrentDate() {
        return new Date(System.currentTimeMillis());
    }
}