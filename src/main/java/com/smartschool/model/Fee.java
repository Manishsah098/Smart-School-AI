package com.smartschool.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Fee {
    public enum Status { PAID, PARTIALLY_PAID, PENDING, OVERDUE }

    private Long id;
    private Long studentId;
    private Long feeStructureId;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal remainingAmount;
    private LocalDate dueDate;
    private Status status;
    private String academicYear;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Transient
    private String studentName;
    private String feeCategory;

    public Fee() {}

    public Long getId()                         { return id; }
    public void setId(Long id)                  { this.id = id; }
    public Long getStudentId()                  { return studentId; }
    public void setStudentId(Long sid)          { this.studentId = sid; }
    public Long getFeeStructureId()             { return feeStructureId; }
    public void setFeeStructureId(Long fid)     { this.feeStructureId = fid; }
    public BigDecimal getTotalAmount()          { return totalAmount; }
    public void setTotalAmount(BigDecimal t)    { this.totalAmount = t; }
    public BigDecimal getPaidAmount()           { return paidAmount; }
    public void setPaidAmount(BigDecimal p)     { this.paidAmount = p; }
    public BigDecimal getRemainingAmount()      { return remainingAmount; }
    public void setRemainingAmount(BigDecimal r){ this.remainingAmount = r; }
    public LocalDate getDueDate()               { return dueDate; }
    public void setDueDate(LocalDate dueDate)   { this.dueDate = dueDate; }
    public Status getStatus()                   { return status; }
    public void setStatus(Status status)        { this.status = status; }
    public String getAcademicYear()             { return academicYear; }
    public void setAcademicYear(String y)       { this.academicYear = y; }
    public LocalDateTime getCreatedAt()         { return createdAt; }
    public void setCreatedAt(LocalDateTime c)   { this.createdAt = c; }
    public LocalDateTime getUpdatedAt()         { return updatedAt; }
    public void setUpdatedAt(LocalDateTime u)   { this.updatedAt = u; }
    public String getStudentName()              { return studentName; }
    public void setStudentName(String n)        { this.studentName = n; }
    public String getFeeCategory()              { return feeCategory; }
    public void setFeeCategory(String fc)       { this.feeCategory = fc; }
}
