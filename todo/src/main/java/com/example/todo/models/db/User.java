package com.example.todo.models.db;

import com.example.todo.constants.enums.UserStatus;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "t_user")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    @OneToMany(mappedBy = "user")
    private Set<Task> tasks;
    @OneToMany(mappedBy = "user")
    private Set<RefreshToken> refreshTokens;
    @OneToMany(mappedBy = "user")
    private Set<Category> categories;
    @UpdateTimestamp
    private Date updatedAt;
    @CreationTimestamp
    private Date createdAt;
}
