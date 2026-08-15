package com.smartschool.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

public class Exam {
    public enum Type { UNIT_TEST, MID_TERM, FINAL, PRACTICAL }

    private Long id;
    private String name;
    private Type type;
    private Long classId;
    private Long sectionId;
    private Long subjectId;
    private BigDecimal maxMarks;
    private LocalDate examDate;
    private String academicYear;
    private Long createdBy;
    private boolean active;
    private LocalDateTime createdAt;

    // Transient
    private String className;
    private String sectionName;
    private String subjectName;

    public Exam() {}

    public Long getId()                         { return id; }
    public void setId(Long id)                  { this.id = id; }
    public String getName()                     { return name; }
    public void setName(String name)            { this.name = name; }
    public Type getType()                       { return type; }
    public void setType(Type type)              { this.type = type; }
    public Long getClassId()                    { return classId; }
    public void setClassId(Long classId)        { this.classId = classId; }
    public Long getSectionId()                  { return sectionId; }
    public void setSectionId(Long sectionId)    { this.sectionId = sectionId; }
    public Long getSubjectId()                  { return subjectId; }
    public void setSubjectId(Long subjectId)    { this.subjectId = subjectId; }
    public BigDecimal getMaxMarks()             { return maxMarks; }
    public void setMaxMarks(BigDecimal mm)      { this.maxMarks = mm; }
    public LocalDate getExamDate()              { return examDate; }
    public void setExamDate(LocalDate d)        { this.examDate = d; }
    public String getAcademicYear()             { return academicYear; }
    public void setAcademicYear(String y)       { this.academicYear = y; }
    public Long getCreatedBy()                  { return createdBy; }
    public void setCreatedBy(Long createdBy)    { this.createdBy = createdBy; }
    public boolean isActive()                   { return active; }
    public void setActive(boolean active)       { this.active = active; }
    public LocalDateTime getCreatedAt()         { return createdAt; }
    public void setCreatedAt(LocalDateTime c)   { this.createdAt = c; }
    public String getClassName()                { return className; }
    public void setClassName(String cn)         { this.className = cn; }
    public String getSectionName()              { return sectionName; }
    public void setSectionName(String sn)       { this.sectionName = sn; }
    public String getSubjectName()              { return subjectName; }
    public void setSubjectName(String sn)       { this.subjectName = sn; }
}
