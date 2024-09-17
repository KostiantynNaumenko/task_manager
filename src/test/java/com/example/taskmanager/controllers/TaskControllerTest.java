package com.example.taskmanager.controllers;

import com.example.taskmanager.dtos.task.TaskDto;
import com.example.taskmanager.dtos.task.TaskMappingUtils;
import com.example.taskmanager.enteties.enums.Status;
import com.example.taskmanager.services.TaskService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private TaskService taskServiceMock;
    @MockBean
    private TaskMappingUtils taskMappingUtilsMock;

    private static final String POST_JSON = """
            {
                "id" : 1,
                "title" : "title",
                "description" : "description",
                "status" : "TODO",
                "created" : null,
                "updated" : null
            }""";
    private static final String INVALID_POST_JSON = "INVALID INPUT STRING";


    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MockitoAnnotations.openMocks(TaskControllerTest.class);
        assertNotNull(taskServiceMock);
        assertNotNull(mvc);
    }

    @Test
    void getTasks_validOutput() throws Exception {
        when(taskServiceMock.getAllTasks()).thenReturn(new ArrayList<>());
        mvc.perform(get("/tasks").with(user("admin").password("pass")))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    void addTask_validInput() throws Exception {
        when(taskMappingUtilsMock.taskRequestToDto(any())).thenReturn(new TaskDto());
        when(taskServiceMock.createTask(any())).thenReturn(1L);
        mvc.perform(post("/tasks")
                        .with(user("admin").password("password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(POST_JSON).characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    void addTask_invalidInput() throws Exception {
        when(taskMappingUtilsMock.taskRequestToDto(any())).thenReturn(new TaskDto());
        when(taskServiceMock.createTask(any())).thenReturn(1L);
        mvc.perform(post("/tasks")
                        .with(user("admin").password("pass"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(INVALID_POST_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addTask_entityExists() throws Exception {
        when(taskMappingUtilsMock.taskRequestToDto(any())).thenReturn(new TaskDto());
        when(taskServiceMock.createTask(any())).thenThrow(new EntityExistsException());
        mvc.perform(post("/tasks")
                        .with(user("admin").password("pass"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(POST_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Task with title: title already exists"));
    }

    @Test
    void addTask_tooManyTasks() throws Exception {
        when(taskServiceMock.createTask(any())).thenThrow(new UnsupportedOperationException());
        mvc.perform(post("/tasks")
                        .with(user("admin").password("pass"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(POST_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Too many tasks with this status: TODO"));
    }

    @Test
    void updateTaskStatus_success() throws Exception {
        when(taskServiceMock.updateTaskStatus(1L, Status.IN_PROGRESS)).thenReturn(new TaskDto(
                1L, null, null, null, null, null));
        mvc.perform(put("/tasks")
                        .with(user("admin").password("pass"))
                        .param("id", String.valueOf(1))
                        .param("status", "IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(content().string("Task with ID: 1 was successfully updated"));
    }

    @Test
    void updateTaskStatus_notFound() throws Exception {
        when(taskServiceMock.updateTaskStatus(1L, Status.IN_PROGRESS)).thenThrow(new EntityNotFoundException());
        mvc.perform(put("/tasks")
                        .with(user("admin").password("pass"))
                        .param("id", String.valueOf(1))
                        .param("status", "IN_PROGRESS"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Task with ID: 1 was not found to be updated"));
    }
    //Task with ID: %s can't be updated because to many task with status: %s
    @Test
    void updateTaskStatus_tooManyTasks() throws Exception {
        when(taskServiceMock.updateTaskStatus(1L, Status.IN_PROGRESS)).thenThrow(new UnsupportedOperationException());
        mvc.perform(put("/tasks")
                        .with(user("admin").password("pass"))
                        .param("id", String.valueOf(1))
                        .param("status", "IN_PROGRESS"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Task with ID: 1 can't be updated because to many task" +
                        " with status: IN_PROGRESS"));
    }

    @Test
    void updateTaskStatus_wrongParams() throws Exception {
        when(taskServiceMock.updateTaskStatus(1L, Status.IN_PROGRESS)).thenThrow(new EntityNotFoundException());
        mvc.perform(put("/tasks")
                        .with(user("admin").password("pass"))
                        .param("id", String.valueOf(1))
                        .param("status", "WRONG_STATUS"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Task with ID: 1 can't be updated because status:" +
                        " WRONG_STATUS is not valid"));
    }

    @Test
    void patchTaskFields_success() throws Exception {
        when(taskServiceMock.updateTaskFields(1L, "title", "description")).thenReturn(new TaskDto(
                1L, "title", "description", null, null, null));
        mvc.perform(patch("/tasks")
                        .with(user("admin").password("pass"))
                        .param("id", String.valueOf(1))
                        .param("title", "title")
                        .param("description", "description"))
                .andExpect(status().isOk())
                .andExpect(content().string("Task with ID: 1 was successfully updated"));
    }

    @Test
    void patchTaskFields_notFound() throws Exception {
        when(taskServiceMock.updateTaskFields(1L, "title", "description")).thenThrow(new EntityNotFoundException());
        mvc.perform(patch("/tasks")
                        .with(user("admin").password("pass"))
                        .param("id", String.valueOf(1))
                        .param("title", "title")
                        .param("description", "description"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Task with ID: 1 was not found to be updated"));
    }

    @Test
    void deleteTask_success() throws Exception {
        mvc.perform(delete("/tasks")
                        .with(user("admin").password("pass"))
                        .param("id", String.valueOf(1)))
                .andExpect(status().isOk())
                .andExpect(content().string("Task with ID: 1 was successfully deleted"));
    }

    @Test
    void deleteTask_notFound() throws Exception {
        Mockito.doThrow(new EntityNotFoundException()).when(taskServiceMock).deleteTask(1L);
        mvc.perform(delete("/tasks")
                        .with(user("admin").password("pass"))
                        .param("id", String.valueOf(1)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Task with ID: 1 was not found to be deleted"));
    }
}