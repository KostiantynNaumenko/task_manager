package com.example.taskmanager.repositories;

import com.example.taskmanager.enteties.Task;
import com.example.taskmanager.enteties.enums.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TaskRepository underTest;

    private Task task;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setTitle("Task Title");
        task.setDescription("Task Description");
        task.setStatus(Status.TODO);
        task.setCreated(getCurrentDate());
        task.setUpdated(getCurrentDate());
    }

    @Test
    void givenTaskObject_whenSave_thenCountTaskByStatusShouldChange() {
        //Given
        int givenInTodo = underTest.countTasksByStatus(Status.TODO);

        // When
        underTest.save(task);
        int whenInTodo = underTest.countTasksByStatus(Status.TODO);

        //Then
        assertEquals(0, givenInTodo);
        assertEquals(1, whenInTodo);
    }

    private Date getCurrentDate() {
        return new Date(System.currentTimeMillis());
    }
}