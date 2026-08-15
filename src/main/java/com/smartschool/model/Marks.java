package com.smartschool.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Marks {
    private Long id;
    private Long examId;
    private Long studentId;
    private BigDecimal marksObtained;
    private BigDecimal percentage;
    private String grade;
    private Boolean isPass;
    private String remarks;
    private Long enteredBy;
    private LocalDateTime enteredAt;
    private LocalDateTime updatedAt;

    // Transient
    private String studentName;
    private String examName;
    private BigDecimal maxMarks;

    public Marks() {}

    public Long getId()                         { return id; }
    public void setId(Long id)                  { this.id = id; }
    public Long getExamId()                     { return examId; }
    public void setExamId(Long examId)          { this.examId = examId; }
    public Long getStudentId()                  { return studentId; }
    public void setStudentId(Long sid)          { this.studentId = sid; }
    public BigDecimal getMarksObtained()        { return marksObtained; }
    public void setMarksObtained(BigDecimal m)  { this.marksObtained = m; }
    public BigDecimal getPercentage()           { return percentage; }
    public void setPercentage(BigDecimal p)     { this.percentage = p; }
    public String getGrade()                    { return grade; }
    public void setGrade(String grade)          { this.grade = grade; }
    public Boolean getIsPass()                  { return isPass; }
    public void setIsPass(Boolean isPass)       { this.isPass = isPass; }
    public String getRemarks()                  { return remarks; }
    public void setRemarks(String remarks)      { this.remarks = remarks; }
    public Long getEnteredBy()                  { return enteredBy; }
    public void setEnteredBy(Long enteredBy)    { this.enteredBy = enteredBy; }
    public LocalDateTime getEnteredAt()         { return enteredAt; }
    public void setEnteredAt(LocalDateTime e)   { this.enteredAt = e; }
    public LocalDateTime getUpdatedAt()         { return updatedAt; }
    public void setUpdatedAt(LocalDateTime u)   { this.updatedAt = u; }
    public String getStudentName()              { return studentName; }
    public void setStudentName(String n)        { this.studentName = n; }
    public String getExamName()                 { return examName; }
    public void setExamName(String n)           { this.examName = n; }
    public BigDecimal getMaxMarks()             { return maxMarks; }
    public void setMaxMarks(BigDecimal m)       { this.maxMarks = m; }
}
