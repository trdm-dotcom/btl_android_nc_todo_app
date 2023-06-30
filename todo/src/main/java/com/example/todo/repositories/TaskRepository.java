package com.example.todo.repositories;

import com.example.todo.constants.enums.Priority;
import com.example.todo.constants.enums.TaskStatus;
import com.example.todo.models.db.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    default Page<Task> findTaskBy(Long userId, String start, String end, Priority priority, TaskStatus status, Long organization, Pageable pageable) {
        return this.findAll(new Specification<Task>() {
            @Override
            public Predicate toPredicate(Root<Task> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.join("assignees").get("id"), userId)));
                if (organization != null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("organization").get("id"), organization)));
                }
                if (start != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                    LocalDate date = LocalDate.parse(start, formatter);
                    System.out.println(date);
                    predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("endDate"), date)));
                }
                if (end != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                    LocalDate date = LocalDate.parse(end, formatter);
                    System.out.println(date);
                    predicates.add(criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(root.get("startDate"), date)));
                }
                if (priority != null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("priority"), priority)));
                }
                if (status != null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("status"), status)));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);
    }

    @Query("FROM Task WHERE remind IS true AND endDate >= :date AND startDate <= :date")
    Set<Task> findByRemindTrueAndDate(LocalDate date);
}
