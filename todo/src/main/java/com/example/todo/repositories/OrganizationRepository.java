package com.example.todo.repositories;

import com.example.todo.models.IOrganization;
import com.example.todo.models.db.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    @Query(value = "WITH tb1 AS (SELECT * FROM `android-nc`.t_task t INNER JOIN `android-nc`.task_assignees ta ON t.id = ta.task_id) " +
            "SELECT o.id, o.name, " +
            "    COUNT(CASE WHEN t.status != 'DONE' THEN 1 END) AS `left`, " +
            "    COUNT(CASE WHEN t.status = 'DONE' THEN 1 END) AS done " +
            "FROM `android-nc`.t_organization o " +
            "INNER JOIN `android-nc`.user_organizations uo ON o.id = uo.organization_id " +
            "INNER JOIN `android-nc`.t_user u ON u.id = uo.user_id " +
            "LEFT JOIN tb1 t ON t.organization_id = o.id AND t.user_id = u.id " +
            "GROUP BY o.id, o.name;", nativeQuery = true)
    Set<IOrganization> getOrganizationsByUsersId(Long userId);
}
