package com.remotejob.planservice.service;

import com.remotejob.planservice.amqp.dto.InvoiceStatusUpdateEvent;
import com.remotejob.planservice.amqp.dto.PlansToCreateEvent;
import com.remotejob.planservice.entity.Plan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceWorkerService {

    private final PlanService planService;

    public void handleInvoiceStatusUpdate(InvoiceStatusUpdateEvent event) {
        log.info("InvoiceStatusUpdateEvent received: userId={}, invoiceId={}, status={}",
                event.userId, event.invoiceId, event.status);
        Optional<Plan> existing = planService.getByUserIdAndInvoiceId(event.userId, event.invoiceId);
        if (existing.isEmpty()) {
            log.warn("No plan found for userId={} and invoiceId={}", event.userId, event.invoiceId);
            return;
        }
        Plan plan = existing.get();
        if (event.status != null) plan.setStatus(event.status);
        if (event.isActive != null) plan.setIsActive(event.isActive);
        if (event.expiresAt != null) plan.setExpiresAt(event.expiresAt);
        planService.createOrUpdate(plan);
        log.info("Plan {} updated from invoice status event", plan.getId());
    }

    public void handlePlansToCreate(PlansToCreateEvent event) {
        log.info("PlansToCreateEvent received: userId={}, invoiceId={}", event.userId, event.invoiceId);
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
        Plan saved = planService.createOrUpdate(plan);
        log.info("Plan {} saved/updated from PlansToCreateEvent", saved.getId());
    }
}
