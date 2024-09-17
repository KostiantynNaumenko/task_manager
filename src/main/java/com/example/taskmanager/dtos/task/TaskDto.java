package com.example.taskmanager.dtos.task;

import com.example.taskmanager.enteties.enums.Status;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.sql.Date;

@Data
@Getter
@Setter
public class TaskDto {

    private Long id;
    private String title;
    private String description;
    private Date created;
    private Date updated;
    private Status status;

    public TaskDto() {
    }

    public TaskDto(String title, String description)
    {
        this.title = title;
        this.description = description;
    }

    public TaskDto(Long id, String title, String description, Date created, Date updated, Status status)
    {
        this.id = id;
        this.title = title;
        this.description = description;
        this.created = created;
        this.updated = updated;
        this.status = status;
    }
}
