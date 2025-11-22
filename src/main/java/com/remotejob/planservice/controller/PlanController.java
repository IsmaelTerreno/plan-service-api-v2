package com.remotejob.planservice.controller;

import com.remotejob.planservice.dto.ResponseAPI;
import com.remotejob.planservice.entity.Plan;
import com.remotejob.planservice.service.PlanService;
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
     * of plan entities within the system.
     */
    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    /**
     * Retrieves a plan by its unique identifier.
     *
     * @param id The UUID of the plan to retrieve.
     * @return A ResponseAPI object containing an Optional with the Job if found,
     * or an empty Optional if not found.
     */
    @GetMapping("/{id}")
    public ResponseAPI<Optional<Plan>> getJobById(@PathVariable(value = "id") UUID id) {
        return new ResponseAPI<>(
                "Success",
                this.planService.getById(id)
        );
    }

    /**
     * Retrieves a list of plans associated with a specific user ID.
     *
     * @param userId The ID of the user whose plans are to be retrieved.
     * @return A ResponseAPI object containing a list of plans associated with the specified user ID.
     */
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/user/{userId}")
    public ResponseAPI<List<Plan>> getByUserId(@PathVariable(value = "userId") String userId) {
        return new ResponseAPI<>(
                "Success",
                this.planService.getByUserId(userId)
        );
    }

    /**
     * Creates a new plan or updates an existing one.
     *
     * @param plan The Job object to be created or updated.
     * @return A ResponseAPI object containing the created or updated Job.
     */
    @PreAuthorize("hasAuthority('USER')")
    @PostMapping()
    public ResponseAPI<Plan> create(@RequestBody Plan plan) {
        return new ResponseAPI<>(
                "Success",
                planService.createOrUpdate(plan)
        );
    }

    /**
     * Updates an existing plan.
     *
     * @param plan The Job object to be updated.
     * @return A ResponseAPI object containing the updated Job.
     */
    @PreAuthorize("hasAuthority('USER')")
    @PutMapping()
    public ResponseAPI<Plan> update(@RequestBody Plan plan) {
        return new ResponseAPI<>(
                "Success",
                planService.createOrUpdate(plan)
        );
    }

    /**
     * Deletes a plan by its unique identifier.
     *
     * @param id The UUID of the plan to delete.
     * @return A ResponseAPI object containing a success message and no data.
     */
    @PreAuthorize("hasAuthority('USER')")
    @DeleteMapping("/{id}")
    public ResponseAPI<Plan> delete(@PathVariable(value = "id") UUID id) {
        planService.delete(id);
        return new ResponseAPI<>(
                "Plan deleted successfully",
                null
        );
    }
}
