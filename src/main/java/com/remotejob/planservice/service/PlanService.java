package com.remotejob.planservice.service;

import com.remotejob.planservice.entity.Plan;
import com.remotejob.planservice.repository.PlanRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class for managing job-related operations. This class interacts with the
 * PlanRepository to perform CRUD operations and provides methods to retrieve jobs based
 * on various criteria.
 */
@Service
public class PlanService {
    private final PlanRepository planRepository;


    public PlanService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    /**
     * Retrieves all job entities from the repository.
     *
     * @return A list of all jobs available in the repository.
     */
    public List<Plan> findAll() {
        return this.planRepository.findAll();
    }

    /**
     * Creates a new job or updates an existing one in the repository.
     *
     * @param plan The Job object to be created or updated.
     * @return The saved Job object.
     */
    public Plan createOrUpdate(Plan plan) {
        return this.planRepository.save(plan);
    }

    /**
     * Deletes a job by its unique identifier.
     *
     * @param id The UUID of the job to delete.
     */
    public void delete(UUID id) {
        this.planRepository.deleteById(id);
    }

    /**
     * Retrieves a job by its unique identifier.
     *
     * @param id The UUID of the job to retrieve.
     * @return An Optional containing the Job if found, or an empty Optional if not found.
     */
    public Optional<Plan> getJobById(UUID id) {
        return this.planRepository.findById(id);
    }

    /**
     * Retrieves a list of jobs associated with a specific user ID.
     *
     * @param userId The ID of the user whose jobs are to be retrieved.
     * @return A list of jobs associated with the specified user ID.
     */
    public List<Plan> getJobsByUserId(String userId) {
        return this.planRepository.findByUserId(userId);
    }
}
