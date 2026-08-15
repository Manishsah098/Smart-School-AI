package com.smartschool.model;

import java.time.LocalDateTime;

/**
 * An AI agent's recommendation awaiting human approval.
 * Critical: status must be APPROVED before any action executes.
 */
public class AgentRecommendation {
    public enum Priority { LOW, MEDIUM, HIGH, CRITICAL }
    public enum Status { PENDING_APPROVAL, APPROVED, REJECTED, EXECUTED }

    private Long id;
    private Long decisionId;
    private String agentName;
    private String title;
    private String recommendation;
    private Priority priority;
    private Double confidence;
    private Status status;
    private Long reviewedBy;
    private LocalDateTime reviewedAt;
    private String rejectionReason;
    private LocalDateTime createdAt;

    // Transient
    private String reviewerName;

    public AgentRecommendation() {
        this.status = Status.PENDING_APPROVAL;
    }

    public Long getId()                             { return id; }
    public void setId(Long id)                      { this.id = id; }
    public Long getDecisionId()                     { return decisionId; }
    public void setDecisionId(Long decisionId)      { this.decisionId = decisionId; }
    public String getAgentName()                    { return agentName; }
    public void setAgentName(String agentName)      { this.agentName = agentName; }
    public String getTitle()                        { return title; }
    public void setTitle(String title)              { this.title = title; }
    public String getRecommendation()               { return recommendation; }
    public void setRecommendation(String rec)       { this.recommendation = rec; }
    public Priority getPriority()                   { return priority; }
    public void setPriority(Priority priority)      { this.priority = priority; }
    public Double getConfidence()                   { return confidence; }
    public void setConfidence(Double confidence)    { this.confidence = confidence; }
    public Status getStatus()                       { return status; }
    public void setStatus(Status status)            { this.status = status; }
    public Long getReviewedBy()                     { return reviewedBy; }
    public void setReviewedBy(Long reviewedBy)      { this.reviewedBy = reviewedBy; }
    public LocalDateTime getReviewedAt()            { return reviewedAt; }
    public void setReviewedAt(LocalDateTime r)      { this.reviewedAt = r; }
    public String getRejectionReason()              { return rejectionReason; }
    public void setRejectionReason(String rr)       { this.rejectionReason = rr; }
    public LocalDateTime getCreatedAt()             { return createdAt; }
    public void setCreatedAt(LocalDateTime c)       { this.createdAt = c; }
    public String getReviewerName()                 { return reviewerName; }
    public void setReviewerName(String rn)          { this.reviewerName = rn; }
}
