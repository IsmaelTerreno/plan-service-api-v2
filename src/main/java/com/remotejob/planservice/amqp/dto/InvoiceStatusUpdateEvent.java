package com.remotejob.planservice.amqp.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(name = "InvoiceStatusUpdateEvent", description = "Event payload to update plan status based on invoice changes")
public class InvoiceStatusUpdateEvent {
    @Schema(description = "User ID related to the plan", required = true)
    public String userId;

    @Schema(description = "Invoice ID related to the plan", required = true)
    public UUID invoiceId;

    @Schema(description = "New status for the related plan", required = true)
    public String status;

    @Schema(description = "Whether the plan is active after the update")
    public Boolean isActive;

    @Schema(description = "Optional new expiration time for the plan")
    public Instant expiresAt;
}
