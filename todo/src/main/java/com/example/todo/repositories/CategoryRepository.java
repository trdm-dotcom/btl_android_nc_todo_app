package com.example.todo.repositories;

import com.example.todo.models.db.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Set<Category> findByUserId(Long userId);
}
