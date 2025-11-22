package com.remotejob.planservice.dto;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

@Schema(name = "Plan", description = "Subscription plan DTO")
public class PlanDto {
    @Schema(description = "Unique ID in the DB for the plan")
    public UUID id;

    @NotBlank
    @Schema(description = "User ID related to this plan")
    public String userId;

    @NotNull
    @Schema(description = "Invoice ID related to this plan")
    public UUID invoiceId;

    @NotBlank
    @Schema(description = "General description of this plan")
    public String description;

    @NotNull
    @Schema(description = "If this plan is active or not")
    public Boolean isActive;

    @NotNull
    @Schema(description = "The items payload for the plan", implementation = JsonNode.class)
    public JsonNode items;

    @NotBlank
    @Schema(description = "Current status of this plan")
    public String status;

    @NotNull
    @Schema(description = "Duration in days of this plan")
    public Integer durationInDays;

    @Schema(description = "Expiration date of the plan", nullable = true)
    public Instant expiresAt;
}
