package com.remotejob.jobservice.repository;

import com.remotejob.jobservice.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for managing Job entities.
 * Provides methods to perform CRUD operations and execute custom queries.
 */
@Repository
public interface PlanRepository extends JpaRepository<Plan, UUID> {
    /**
     * Retrieves a list of jobs that match the provided search text.
     * The search is performed on the "title" and "detail" columns using full-text search.
     *
     * @param textToSearch The text to search for within the job title and detail fields.
     * @return A list of jobs that match the search criteria.
     */
    @Query(value = "SELECT * FROM plan WHERE to_tsvector(title) @@ to_tsquery(:textToSearch) OR to_tsvector(detail::text) @@ to_tsquery(:textToSearch)", nativeQuery = true)
    List<Plan> findBySearch(@Param("textToSearch") String textToSearch);

    /**
     * Retrieves a list of jobs associated with a specific user ID.
     *
     * @param id The ID of the user whose jobs are to be retrieved.
     * @return A list of jobs associated with the specified user ID.
     */
    List<Plan> findByUserId(String id);
}
