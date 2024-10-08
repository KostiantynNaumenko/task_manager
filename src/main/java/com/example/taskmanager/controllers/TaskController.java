package com.example.taskmanager.controllers;

import com.example.taskmanager.dtos.task.TaskDto;
import com.example.taskmanager.dtos.task.TaskMappingUtils;
import com.example.taskmanager.dtos.task.TaskRequest;
import com.example.taskmanager.enteties.enums.Status;
import com.example.taskmanager.services.TaskService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/tasks")
public class TaskController {

    private final static String UPDATED = "Task with ID: %s was successfully updated";
    private final static String DELETED = "Task with ID: %s was successfully deleted";
    private final static String NOT_DELETED = "Task with ID: %s was not found to be deleted";
    private final static String NOT_UPDATED_BECAUSE_NOT_FOUND = "Task with ID: %s was not found to be updated";
    private final static String NOT_UPDATED_BECAUSE_WRONG_FORMAT = "Task with ID: %s can't be updated because status: %s is not valid";
    private final static String NOT_UPDATED_BECAUSE_TOO_MANY_TASKS = "Task with ID: %s can't be updated because to many task with status: %s";
    private final static String ALREADY_EXISTS = "Task with title: %s already exists";
    private final static String TO_MANY_TASKS = "Too many tasks with this status: %s";

    private final TaskService taskService;
    private final TaskMappingUtils taskMappingUtils;

    public TaskController(TaskService taskService, TaskMappingUtils taskMappingUtils) {
        this.taskService = taskService;
        this.taskMappingUtils = taskMappingUtils;
    }

    @GetMapping
    public List<TaskDto> getTasks() {
        return taskService.getAllTasks();
    }

    @PostMapping(consumes="application/json")
    public String addTask(@RequestBody TaskRequest taskRequest, HttpServletResponse response) {
        try {
            Long id = taskService.createTask(taskMappingUtils.taskRequestToDto(taskRequest));
            return id.toString();
        } catch (EntityExistsException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return String.format(ALREADY_EXISTS, taskRequest.getTitle());
        } catch (UnsupportedOperationException e)
        {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return String.format(TO_MANY_TASKS, Status.TODO);
        }
    }

    @PutMapping
    public String updateTaskStatus(@RequestParam Long id,
                                   @RequestParam String status,
                                   HttpServletResponse response) {
        Status statusEnum;
        try {
            statusEnum = Status.valueOf(status);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return String.format(NOT_UPDATED_BECAUSE_WRONG_FORMAT, id, status);
        }

        try {
            taskService.updateTaskStatus(id, statusEnum);
            return String.format(UPDATED, id);
        } catch (EntityNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return String.format(NOT_UPDATED_BECAUSE_NOT_FOUND, id);
        } catch (UnsupportedOperationException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return String.format(NOT_UPDATED_BECAUSE_TOO_MANY_TASKS, id, status);
        }
    }

    @PatchMapping
    public String patchTaskFields(@RequestParam Long id,
                                  @RequestParam String title,
                                  @RequestParam String description,
                                  HttpServletResponse response) {
        try {
            taskService.updateTaskFields(id, title, description);
            return String.format(UPDATED, id);
        } catch (EntityNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return String.format(NOT_UPDATED_BECAUSE_NOT_FOUND, id);
        }
    }

    @DeleteMapping
    public String deleteTask(@RequestParam Long id, HttpServletResponse response) {
       try {
           taskService.deleteTask(id);
           return String.format(DELETED, id);
       } catch (EntityNotFoundException e) {
           response.setStatus(HttpServletResponse.SC_NOT_FOUND);
           return String.format(NOT_DELETED, id);
       }
    }
}
