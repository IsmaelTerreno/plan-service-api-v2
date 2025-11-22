package com.remotejob.jobservice.controller;

import com.remotejob.jobservice.dto.ResponseAPI;
import com.remotejob.jobservice.entity.Plan;
import com.remotejob.jobservice.service.PlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Rest controller to manage job-related operations.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/plan")
public class PlanController {
    /**
     * Represents a service responsible for managing job-related operations.
     * This service handles the creation, update, retrieval, and deletion
     * of job entities within the system.
     */
    private final PlanService jobService;

    public PlanController(PlanService jobService) {
        this.jobService = jobService;
    }

    /**
     * Endpoint to retrieve a list of jobs based on a search text.
     *
     * @param textToSearch The string to be used as the search criteria.
     * @return A ResponseAPI object containing a list of jobs that match the search criteria.
     */
    @GetMapping("/search")
    public ResponseAPI<List<Plan>> getJobsBySearch(@RequestParam String textToSearch) {
        return new ResponseAPI<>(
                "Success",
                this.jobService.getJobsBySearch(textToSearch)
        );
    }

    /**
     * Retrieves a job by its unique identifier.
     *
     * @param id The UUID of the job to retrieve.
     * @return A ResponseAPI object containing an Optional with the Job if found,
     * or an empty Optional if not found.
     */
    @GetMapping("/{id}")
    public ResponseAPI<Optional<Plan>> getJobById(@PathVariable(value = "id") UUID id) {
        return new ResponseAPI<>(
                "Success",
                this.jobService.getJobById(id)
        );
    }

    /**
     * Retrieves a list of jobs associated with a specific user ID.
     *
     * @param userId The ID of the user whose jobs are to be retrieved.
     * @return A ResponseAPI object containing a list of jobs associated with the specified user ID.
     */
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/user/{userId}")
    public ResponseAPI<List<Plan>> getJobsByUserId(@PathVariable(value = "userId") String userId) {
        return new ResponseAPI<>(
                "Success",
                this.jobService.getJobsByUserId(userId)
        );
    }

    /**
     * Creates a new job or updates an existing one.
     *
     * @param plan The Job object to be created or updated.
     * @return A ResponseAPI object containing the created or updated Job.
     */
    @PreAuthorize("hasAuthority('USER')")
    @PostMapping()
    public ResponseAPI<Plan> createJob(@RequestBody Plan plan) {
        return new ResponseAPI<>(
                "Success",
                jobService.createOrUpdate(plan)
        );
    }

    /**
     * Updates an existing job.
     *
     * @param plan The Job object to be updated.
     * @return A ResponseAPI object containing the updated Job.
     */
    @PreAuthorize("hasAuthority('USER')")
    @PutMapping()
    public ResponseAPI<Plan> updateJob(@RequestBody Plan plan) {
        return new ResponseAPI<>(
                "Success",
                jobService.createOrUpdate(plan)
        );
    }

    /**
     * Deletes a job by its unique identifier.
     *
     * @param id The UUID of the job to delete.
     * @return A ResponseAPI object containing a success message and no data.
     */
    @PreAuthorize("hasAuthority('USER')")
    @DeleteMapping("/{id}")
    public ResponseAPI<Plan> deleteJob(@PathVariable(value = "id") UUID id) {
        jobService.delete(id);
        return new ResponseAPI<>(
                "Plan deleted successfully",
                null
        );
    }
}
