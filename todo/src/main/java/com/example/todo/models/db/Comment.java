package com.example.todo.models.db;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "t_comment")
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name="task_id")
    private Task task;
}
