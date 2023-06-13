package com.example.todo.models.db;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "t_refresh_token")
@Data
public class RefreshToken {
    @Id
    private String token;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private Date expiredAt;
    @CreationTimestamp
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt;
}
