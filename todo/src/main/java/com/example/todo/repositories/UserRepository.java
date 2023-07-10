package com.example.todo.repositories;

import com.example.todo.models.db.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("FROM User WHERE email LIKE %:email%")
    Set<User> findByEmailLike(String email);

    @Query("FROM User WHERE id in (:ids)")
    Set<User> findByIdIn(Collection<Long> ids);

    @Query("SELECT user FROM User user JOIN user.organizations organization WHERE organization.id = :id")
    Set<User> findByOrganizationId(Long id);
    @Query("FROM User WHERE id NOT IN (SELECT user.id FROM User user JOIN user.organizations organization WHERE organization.id = :id)")
    Set<User> findByOrganizationIdNot(Long id);
    @Query("SELECT user FROM Task task JOIN task.assignees user WHERE task.id = :id")
    Set<User> findByTaskId(Long id);
    @Query("SELECT user FROM User user JOIN user.organizations organization WHERE organization.id = :organizationId " +
            " AND user.id NOT IN (SELECT assignee.id FROM Task task JOIN task.assignees assignee WHERE task.id = :id)")
    Set<User> findByTaskIdNot(Long id, Long organizationId);
    Set<User> findByIdIn(List<Long> ids);
}
