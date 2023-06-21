package com.example.todo.models.db;

import com.example.todo.constants.enums.UserRole;
import com.example.todo.constants.enums.UserStatus;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "t_user")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_organizations",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "organization_id"))
    private Set<Organization> organizations = new HashSet<>();
    @UpdateTimestamp
    private Date updatedAt;
    @CreationTimestamp
    private Date createdAt;
}
