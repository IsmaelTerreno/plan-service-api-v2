package com.remotejob.planservice.service;

import com.remotejob.planservice.amqp.dto.InvoiceStatusUpdateEvent;
import com.remotejob.planservice.amqp.dto.PlansToCreateEvent;
import com.remotejob.planservice.entity.Plan;
import com.remotejob.planservice.util.CorrelationContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceWorkerService {

    private final PlanService planService;

    public void handleInvoiceStatusUpdate(InvoiceStatusUpdateEvent event) {
        Instant start = Instant.now();
        
        // Set correlation context
        CorrelationContext.setInvoicePlanContext(event.userId, event.invoiceId);
        
        try {
            log.info("📨 [INVOICE->PLAN] Status update received | userId={} | invoiceId={} | jobId={}", 
                    event.userId, event.invoiceId, event.jobId != null ? event.jobId : "NOT_PROVIDED");
            log.debug("📋 [INVOICE->PLAN] Event payload | status={} | isActive={} | expiresAt={}",
                    event.status, event.isActive, event.expiresAt);
            
            if (event.jobId != null) {
                log.info("📌 [PLAN-JOB] Job ID detected in status update | jobId={} | invoiceId={} | userId={}", 
                        event.jobId, event.invoiceId, event.userId);
            }

            // Validate required fields
            if (event.userId == null || event.invoiceId == null) {
                log.error("❌ [INVOICE->PLAN] Missing required identifiers | userId={} | invoiceId={}", 
                        event.userId, event.invoiceId);
                return;
            }

            // Find existing plan
            log.debug("🔍 [INVOICE->PLAN] Looking up plan | userId={} | invoiceId={}", 
                    event.userId, event.invoiceId);
            
            Optional<Plan> existing = planService.getByUserIdAndInvoiceId(event.userId, event.invoiceId);
            if (existing.isEmpty()) {
                log.warn("⚠️  [INVOICE->PLAN] Plan not found | userId={} | invoiceId={} | Action: Plan may need to be created first", 
                        event.userId, event.invoiceId);
                return;
            }

            Plan plan = existing.get();
            CorrelationContext.setPlanId(plan.getId().toString());
            
            // Log current state
            log.info("✅ [INVOICE->PLAN] Plan found | planId={} | currentStatus={} | currentIsActive={}", 
                    plan.getId(), plan.getStatus(), plan.getIsActive());

            // Track changes
            String oldStatus = plan.getStatus();
            Boolean oldIsActive = plan.getIsActive();
            String oldJobId = plan.getJobId();
            
            // Apply updates
            if (event.status != null) plan.setStatus(event.status);
            if (event.isActive != null) plan.setIsActive(event.isActive);
            if (event.expiresAt != null) plan.setExpiresAt(event.expiresAt);
            if (event.jobId != null) plan.setJobId(event.jobId);

            // Save updated plan
            log.info("🔄 [INVOICE->PLAN] Updating plan | planId={} | oldStatus={} | newStatus={} | oldIsActive={} | newIsActive={} | oldJobId={} | newJobId={}", 
                    plan.getId(), oldStatus, plan.getStatus(), oldIsActive, plan.getIsActive(),
                    oldJobId != null ? oldJobId : "NULL", plan.getJobId() != null ? plan.getJobId() : "NULL");
            
            Plan saved = planService.createOrUpdate(plan);

            log.info("✅ [INVOICE->PLAN] Plan updated successfully | planId={} | status={} | isActive={} | jobId={}", 
                    saved.getId(), saved.getStatus(), saved.getIsActive(), 
                    saved.getJobId() != null ? saved.getJobId() : "NULL");
            
            if (saved.getJobId() != null && oldJobId == null) {
                log.info("📌 [PLAN-JOB] Plan newly linked to job via status update | planId={} | jobId={}", 
                        saved.getId(), saved.getJobId());
            } else if (saved.getJobId() != null) {
                log.info("📌 [PLAN-JOB] Plan job link maintained | planId={} | jobId={}", 
                        saved.getId(), saved.getJobId());
            }
            
        } catch (Exception ex) {
            log.error("❌ [INVOICE->PLAN] Error updating plan | userId={} | invoiceId={} | error={}", 
                    event.userId, event.invoiceId, ex.getMessage(), ex);
        } finally {
            long durationMs = Duration.between(start, Instant.now()).toMillis();
            log.info("🎉 [INVOICE->PLAN] Status update completed | userId={} | invoiceId={} | duration={}ms", 
                    event.userId, event.invoiceId, durationMs);
            CorrelationContext.clear();
        }
    }

    public void handlePlansToCreate(PlansToCreateEvent event) {
        Instant start = Instant.now();
        
        // Set correlation context
        CorrelationContext.setInvoicePlanContext(event.userId, event.invoiceId);
        if (event.id != null) {
            CorrelationContext.setPlanId(event.id.toString());
        }
        
        try {
            log.info("📨 [INVOICE->PLAN] Plan creation request received | userId={} | invoiceId={} | jobId={}", 
                    event.userId, event.invoiceId, event.jobId != null ? event.jobId : "NOT_PROVIDED");
            log.debug("📋 [INVOICE->PLAN] Event payload | isActive={} | status={} | durationInDays={} | description={}", 
                    event.isActive, event.status, event.durationInDays, event.description);
            
            if (event.jobId != null) {
                log.info("📌 [PLAN-JOB] Job ID detected in plan creation request | jobId={} | invoiceId={} | userId={}", 
                        event.jobId, event.invoiceId, event.userId);
            } else {
                log.warn("⚠️ [PLAN-JOB] No job ID provided in plan creation request | invoiceId={} | userId={} | This plan will not be linked to a specific job", 
                        event.invoiceId, event.userId);
            }

            // Validate required fields
            if (event.userId == null || event.invoiceId == null) {
                log.error("❌ [INVOICE->PLAN] Missing required identifiers | userId={} | invoiceId={}", 
                        event.userId, event.invoiceId);
                return;
            }

            // Check if plan already exists
            Optional<Plan> existingPlan = planService.getByUserIdAndInvoiceId(event.userId, event.invoiceId);
            boolean isUpdate = existingPlan.isPresent();
            
            if (isUpdate) {
                log.info("🔄 [INVOICE->PLAN] Plan already exists, will update | planId={} | userId={} | invoiceId={}", 
                        existingPlan.get().getId(), event.userId, event.invoiceId);
            } else {
                log.info("🆕 [INVOICE->PLAN] Creating new plan | userId={} | invoiceId={}", 
                        event.userId, event.invoiceId);
            }

            // Build plan object
            Plan plan = new Plan();
            plan.setId(event.id);
            plan.setUserId(event.userId);
            plan.setInvoiceId(event.invoiceId);
            plan.setDescription(event.description);
            plan.setIsActive(event.isActive);
            plan.setItems(event.items);
            plan.setStatus(event.status);
            plan.setDurationInDays(event.durationInDays);
            plan.setExpiresAt(event.expiresAt);
            plan.setJobId(event.jobId);

            // Save plan
            log.info("💾 [INVOICE->PLAN] Saving plan | userId={} | invoiceId={} | jobId={} | isActive={} | status={}", 
                    event.userId, event.invoiceId, event.jobId != null ? event.jobId : "NULL",
                    event.isActive, event.status);
            
            Plan saved = planService.createOrUpdate(plan);
            CorrelationContext.setPlanId(saved.getId().toString());

            log.info("✅ [INVOICE->PLAN] Plan {} successfully | planId={} | userId={} | invoiceId={} | jobId={} | isActive={} | status={}", 
                    isUpdate ? "updated" : "created", 
                    saved.getId(), saved.getUserId(), saved.getInvoiceId(), 
                    saved.getJobId() != null ? saved.getJobId() : "NULL",
                    saved.getIsActive(), saved.getStatus());
            log.debug("📊 [INVOICE->PLAN] Plan details | durationInDays={} | expiresAt={} | itemCount={}", 
                    saved.getDurationInDays(), saved.getExpiresAt(), 
                    saved.getItems() != null && saved.getItems().isArray() ? saved.getItems().size() : 0);
            
            if (saved.getJobId() != null) {
                log.info("📌 [PLAN-JOB] Plan successfully linked to job | planId={} | jobId={} | invoiceId={}", 
                        saved.getId(), saved.getJobId(), saved.getInvoiceId());
            }
            
        } catch (Exception ex) {
            log.error("❌ [INVOICE->PLAN] Error creating/updating plan | userId={} | invoiceId={} | error={}", 
                    event.userId, event.invoiceId, ex.getMessage(), ex);
        } finally {
            long durationMs = Duration.between(start, Instant.now()).toMillis();
            log.info("🎉 [INVOICE->PLAN] Plan creation/update completed | userId={} | invoiceId={} | duration={}ms", 
                    event.userId, event.invoiceId, durationMs);
            CorrelationContext.clear();
        }
    }

}
