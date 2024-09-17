package com.example.taskmanager.repositories;

import com.example.taskmanager.enteties.Task;
import com.example.taskmanager.enteties.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    int countTasksByStatus(Status status);

    boolean existsTasksByTitle(String title);
}
