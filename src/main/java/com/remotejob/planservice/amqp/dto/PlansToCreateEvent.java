package com.remotejob.planservice.amqp.dto;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(name = "PlansToCreateEvent", description = "Event payload to create or update plans from async messages")
public class PlansToCreateEvent {
    public UUID id; // optional for updates
    public String userId;
    public UUID invoiceId;
    public String description;
    public Boolean isActive;
    public JsonNode items;
    public String status;
    public Integer durationInDays;
    public Instant expiresAt;
}
