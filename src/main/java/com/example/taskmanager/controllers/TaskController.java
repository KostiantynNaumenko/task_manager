package com.example.taskmanager.controllers;

import com.example.taskmanager.dtos.task.TaskDto;
import com.example.taskmanager.enteties.enums.Status;
import com.example.taskmanager.services.TaskService;
import lombok.val;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/tasks")
public class TaskController {

    private final static String UPDATED = "Task with ID: %s was successfully updated";
    private final static String DELETED = "Task with ID: %s was successfully deleted";
    private final static String NOT_UPDATED = "Task with ID: %s was not updated";

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskDto> getTasks() {
        return taskService.getAllTasks();
    }

    @PostMapping
    public Long addTask(@RequestBody TaskDto taskDto) {
        return taskService.createTask(taskDto);
    }

    @PutMapping
    public String updateTaskStatus(@RequestParam Long id,
                                   @RequestParam String status) {
        Status statusEnum = Status.valueOf(status);
        val taskDto = taskService.updateTaskStatus(id, statusEnum);
        if (taskDto != null) {
            return String.format(UPDATED, id);
        }
        return String.format(NOT_UPDATED, id);
    }

    @PatchMapping
    public String patchTaskFields(@RequestParam Long id,
                                  @RequestParam String title,
                                  @RequestParam String description) {
        val taskDto = taskService.updateTaskFields(id, title, description);
        if (taskDto != null) {
            return String.format(UPDATED, id);
        }
        return String.format(NOT_UPDATED, id);
    }

    @DeleteMapping
    public String deleteTask(@RequestParam Long id) {
        taskService.deleteTask(id);
        return String.format(DELETED, id);
    }
}
