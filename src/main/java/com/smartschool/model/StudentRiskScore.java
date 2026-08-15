package com.smartschool.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Stores the AI agent's risk assessment for a student over time.
 * Enables trend tracking (LOW → MEDIUM → HIGH) for early interventions.
 */
public class StudentRiskScore {
    public enum RiskLevel { LOW, MEDIUM, HIGH, CRITICAL }

    private Long id;
    private Long studentId;
    private RiskLevel riskLevel;
    private BigDecimal riskScore;
    private BigDecimal attScore;
    private BigDecimal marksScore;
    private BigDecimal trendScore;
    private String riskReason;
    private String assessedBy;
    private LocalDateTime assessedAt;

    // Transient
    private String studentName;

    public StudentRiskScore() {}

    public Long getId()                         { return id; }
    public void setId(Long id)                  { this.id = id; }
    public Long getStudentId()                  { return studentId; }
    public void setStudentId(Long sid)          { this.studentId = sid; }
    public RiskLevel getRiskLevel()             { return riskLevel; }
    public void setRiskLevel(RiskLevel rl)      { this.riskLevel = rl; }
    public BigDecimal getRiskScore()            { return riskScore; }
    public void setRiskScore(BigDecimal rs)     { this.riskScore = rs; }
    public BigDecimal getAttScore()             { return attScore; }
    public void setAttScore(BigDecimal a)       { this.attScore = a; }
    public BigDecimal getMarksScore()           { return marksScore; }
    public void setMarksScore(BigDecimal m)     { this.marksScore = m; }
    public BigDecimal getTrendScore()           { return trendScore; }
    public void setTrendScore(BigDecimal t)     { this.trendScore = t; }
    public String getRiskReason()               { return riskReason; }
    public void setRiskReason(String rr)        { this.riskReason = rr; }
    public String getAssessedBy()               { return assessedBy; }
    public void setAssessedBy(String ab)        { this.assessedBy = ab; }
    public LocalDateTime getAssessedAt()        { return assessedAt; }
    public void setAssessedAt(LocalDateTime a)  { this.assessedAt = a; }
    public String getStudentName()              { return studentName; }
    public void setStudentName(String n)        { this.studentName = n; }
}
