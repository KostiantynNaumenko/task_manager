package com.example.taskmanager.controllers;

import com.example.taskmanager.dtos.task.TaskDto;
import com.example.taskmanager.enteties.enums.Status;
import com.example.taskmanager.services.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/tasks")
public class TaskController {

    private final static String UPDATED = "Task with ID: %s was successfully updated";

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskDto> getTasks() {
        return taskService.getAllTasks();
    }

    @PostMapping
    public String addTask(@RequestBody TaskDto taskDto) {
        return taskService.createTask(taskDto).toString();
    }

    @PutMapping
    public String updateTaskStatus(@RequestParam Long id,
                                   @RequestParam String status) {
        Status statusEnum = Status.valueOf(status);
        return taskService.updateTaskStatus(id, statusEnum).toString();
    }

    @PatchMapping
    public String patchTaskFields(@RequestParam Long id,
                                  @RequestParam String title,
                                  @RequestParam String description) {
        return  taskService.updateTaskFields(id, title, description).toString();
    }
}
