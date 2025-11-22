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
    private final PlanRepository jobRepository;


    public PlanService(PlanRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    /**
     * Retrieves all job entities from the repository.
     *
     * @return A list of all jobs available in the repository.
     */
    public List<Plan> findAll() {
        return this.jobRepository.findAll();
    }

    /**
     * Creates a new job or updates an existing one in the repository.
     *
     * @param plan The Job object to be created or updated.
     * @return The saved Job object.
     */
    public Plan createOrUpdate(Plan plan) {
        return this.jobRepository.save(plan);
    }

    /**
     * Deletes a job by its unique identifier.
     *
     * @param id The UUID of the job to delete.
     */
    public void delete(UUID id) {
        this.jobRepository.deleteById(id);
    }

    /**
     * Retrieves a job by its unique identifier.
     *
     * @param id The UUID of the job to retrieve.
     * @return An Optional containing the Job if found, or an empty Optional if not found.
     */
    public Optional<Plan> getJobById(UUID id) {
        return this.jobRepository.findById(id);
    }

    /**
     * Retrieves a list of jobs associated with a specific user ID.
     *
     * @param userId The ID of the user whose jobs are to be retrieved.
     * @return A list of jobs associated with the specified user ID.
     */
    public List<Plan> getJobsByUserId(String userId) {
        return this.jobRepository.findByUserId(userId);
    }

    /**
     * Retrieves a list of jobs that match the given search text.
     *
     * @param search The string to be used as the search criteria. Spaces in the string will be replaced
     *               with " & " for full-text search syntax compatibility.
     * @return A list of jobs that match the search criteria.
     */
    public List<Plan> getJobsBySearch(String search) {
        String sanitizedSearch = search.replace(" ", " & ");
        return this.jobRepository.findBySearch(sanitizedSearch);
    }
}
