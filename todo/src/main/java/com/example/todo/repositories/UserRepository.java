package com.example.todo.repositories;

import com.example.todo.models.db.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("FROM User WHERE email LIKE %:email%")
    Set<User> findByEmailLike(String email);

    @Query("FROM User WHERE id in (:ids)")
    Set<User> findByIdIn(Collection<Long> ids);
}
