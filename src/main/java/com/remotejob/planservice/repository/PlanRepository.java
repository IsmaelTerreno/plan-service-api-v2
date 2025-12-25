package com.remotejob.planservice.repository;

import com.remotejob.planservice.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing Plan entities.
 */
@Repository
public interface PlanRepository extends JpaRepository<Plan, UUID> {
    /**
     * Retrieves a list of plans associated with a specific user ID.
     */
    List<Plan> findByUserId(String id);

    /**
     * Retrieves a plan by userId and invoiceId.
     */
    Optional<Plan> findByUserIdAndInvoiceId(String userId, UUID invoiceId);

    /**
     * Retrieves a list of plans associated with a specific job ID.
     */
    List<Plan> findByJobId(String jobId);

    /**
     * Retrieves all active plans that haven't expired and have a job ID.
     * Used to find sticky plans - filtering by metadata is done in the service layer.
     * 
     * @param isActive Active status
     * @param expiresAt Current timestamp to filter expired plans
     * @return List of active plans with job IDs, ordered by expiration date descending
     */
    List<Plan> findByIsActiveAndExpiresAtAfterAndJobIdIsNotNullOrderByExpiresAtDesc(Boolean isActive, Instant expiresAt);
}
