package com.remotejob.planservice.service;

import com.remotejob.planservice.dto.PlanPatchDto;
import com.remotejob.planservice.entity.Plan;
import com.remotejob.planservice.repository.PlanRepository;
import com.remotejob.planservice.util.CorrelationContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class for managing job-related operations. This class interacts with the
 * PlanRepository to perform CRUD operations and provides methods to retrieve jobs based
 * on various criteria.
 */
@Slf4j
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
        log.debug("📋 [PLAN] Fetching all plans");
        List<Plan> plans = this.planRepository.findAll();
        log.debug("📋 [PLAN] Found {} plans", plans.size());
        return plans;
    }

    /**
     * Creates a new job or updates an existing one in the repository.
     *
     * @param plan The Job object to be created or updated.
     * @return The saved Job object.
     */
    public Plan createOrUpdate(Plan plan) {
        boolean isNew = (plan.getId() == null);
        
        if (isNew) {
            log.info("💾 [PLAN] Creating new plan | userId={} | invoiceId={} | isActive={}", 
                    plan.getUserId(), plan.getInvoiceId(), plan.getIsActive());
        } else {
            log.info("💾 [PLAN] Updating existing plan | planId={} | userId={} | invoiceId={} | isActive={}", 
                    plan.getId(), plan.getUserId(), plan.getInvoiceId(), plan.getIsActive());
        }
        
        Plan saved = this.planRepository.save(plan);
        CorrelationContext.setPlanId(saved.getId().toString());
        
        if (isNew) {
            log.info("✅ [PLAN] Plan created | planId={} | userId={} | invoiceId={} | status={} | isActive={}", 
                    saved.getId(), saved.getUserId(), saved.getInvoiceId(), saved.getStatus(), saved.getIsActive());
        } else {
            log.info("✅ [PLAN] Plan updated | planId={} | status={} | isActive={}", 
                    saved.getId(), saved.getStatus(), saved.getIsActive());
        }
        
        return saved;
    }

    /**
     * Deletes a job by its unique identifier.
     *
     * @param id The UUID of the job to delete.
     */
    public void delete(UUID id) {
        log.info("🗑️  [PLAN] Deleting plan | planId={}", id);
        this.planRepository.deleteById(id);
        log.info("✅ [PLAN] Plan deleted | planId={}", id);
    }

    /**
     * Retrieves a job by its unique identifier.
     *
     * @param id The UUID of the job to retrieve.
     * @return An Optional containing the Job if found, or an empty Optional if not found.
     */
    public Optional<Plan> getById(UUID id) {
        log.debug("🔍 [PLAN] Looking up plan by ID | planId={}", id);
        Optional<Plan> plan = this.planRepository.findById(id);
        if (plan.isPresent()) {
            log.debug("✅ [PLAN] Plan found | planId={} | userId={} | invoiceId={}", 
                    id, plan.get().getUserId(), plan.get().getInvoiceId());
        } else {
            log.debug("⚠️  [PLAN] Plan not found | planId={}", id);
        }
        return plan;
    }

    /**
     * Retrieves a list of jobs associated with a specific user ID.
     *
     * @param userId The ID of the user whose jobs are to be retrieved.
     * @return A list of jobs associated with the specified user ID.
     */
    public List<Plan> getByUserId(String userId) {
        log.debug("🔍 [PLAN] Looking up plans by user | userId={}", userId);
        List<Plan> plans = this.planRepository.findByUserId(userId);
        log.debug("📋 [PLAN] Found {} plans for user | userId={} | planCount={}", userId, plans.size());
        return plans;
    }

    /**
     * Retrieves a plan by userId and invoiceId.
     */
    public Optional<Plan> getByUserIdAndInvoiceId(String userId, UUID invoiceId) {
        log.debug("🔍 [PLAN] Looking up plan by user and invoice | userId={} | invoiceId={}", userId, invoiceId);
        Optional<Plan> plan = this.planRepository.findByUserIdAndInvoiceId(userId, invoiceId);
        if (plan.isPresent()) {
            log.debug("✅ [PLAN] Plan found | planId={} | userId={} | invoiceId={} | isActive={}", 
                    plan.get().getId(), userId, invoiceId, plan.get().getIsActive());
        } else {
            log.debug("⚠️  [PLAN] Plan not found | userId={} | invoiceId={}", userId, invoiceId);
        }
        return plan;
    }

    /**
     * Retrieves a list of plans associated with a specific job ID.
     *
     * @param jobId The ID of the job whose plans are to be retrieved.
     * @return A list of plans associated with the specified job ID.
     */
    public List<Plan> getByJobId(String jobId) {
        log.debug("🔍 [PLAN] Looking up plans by job | jobId={}", jobId);
        List<Plan> plans = this.planRepository.findByJobId(jobId);
        log.debug("📋 [PLAN] Found {} plans for job | jobId={} | planCount={}", jobId, plans.size());
        return plans;
    }

    /**
     * Partially updates a plan with the non-null fields provided in the patch DTO.
     *
     * @param id    the plan id to update
     * @param patch the fields to apply (only non-null values will be updated)
     * @return the updated plan, or empty if not found
     */
    public Optional<Plan> partialUpdate(UUID id, PlanPatchDto patch) {
        log.info("🔄 [PLAN] Partial update requested | planId={}", id);
        
        Optional<Plan> existingOpt = planRepository.findById(id);
        if (existingOpt.isEmpty()) {
            log.warn("⚠️  [PLAN] Plan not found for partial update | planId={}", id);
            return Optional.empty();
        }
        
        Plan plan = existingOpt.get();
        StringBuilder changes = new StringBuilder();
        
        if (patch.description != null) {
            changes.append("description, ");
            plan.setDescription(patch.description);
        }
        if (patch.isActive != null) {
            changes.append("isActive(").append(patch.isActive).append("), ");
            plan.setIsActive(patch.isActive);
        }
        if (patch.items != null) {
            changes.append("items, ");
            plan.setItems(patch.items);
        }
        if (patch.status != null) {
            changes.append("status(").append(patch.status).append("), ");
            plan.setStatus(patch.status);
        }
        if (patch.durationInDays != null) {
            changes.append("durationInDays(").append(patch.durationInDays).append("), ");
            plan.setDurationInDays(patch.durationInDays);
        }
        if (patch.expiresAt != null) {
            changes.append("expiresAt, ");
            plan.setExpiresAt(patch.expiresAt);
        }
        
        log.info("💾 [PLAN] Applying partial update | planId={} | changes={}", id, changes.toString());
        Plan saved = planRepository.save(plan);
        log.info("✅ [PLAN] Partial update completed | planId={} | isActive={} | status={}", 
                saved.getId(), saved.getIsActive(), saved.getStatus());
        
        return Optional.of(saved);
    }
}
