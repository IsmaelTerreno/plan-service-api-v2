package com.remotejob.jobservice.entity;

import com.fasterxml.jackson.databind.JsonNode;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.UUID;

/**
 * Class representing a job entity, typically used for job postings.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "plan")
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    private UUID id;
    @Column(name = "user_id", nullable = false)
    private String userId;
    @Column(name = "company_id")
    private String companyId;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "category", nullable = false)
    private String category;
    @Column(name = "type", nullable = false)
    private String type;
    @Convert(converter = JsonDynamicConverter.class)
    @Type(JsonBinaryType.class)
    @Column(name = "detail", columnDefinition = "jsonb")
    private JsonNode detail;
    @Column(name = "regional_restrictions")
    private String regionalRestrictions;
    @Column(name = "benefits")
    private String benefits;
    @Column(name = "how_to_apply", nullable = false)
    private String howToApply;
    @Column(name = "salary_from")
    private Integer salaryFrom;
    @Column(name = "salary_to")
    private Integer salaryTo;
    @Column(name = "priority_result")
    private Integer priorityResult;
    // Creation date of the job with default value of current time
    @Column(name = "created_at", nullable = false, updatable = false)
    private Long createdAt;
    // Last update date of the job
    @Column(name = "updated_at", nullable = false)
    private Long updatedAt;
}