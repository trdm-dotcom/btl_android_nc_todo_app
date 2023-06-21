package com.example.todo.models.db;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "t_organization")
@Data
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

}
