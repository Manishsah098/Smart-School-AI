package com.smartschool.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Student entity — maps to `students` table.
 * References User for authentication, Class/Section for academic placement,
 * and Parent for family communication.
 */
public class Student {
    public enum Status { ACTIVE, INACTIVE, GRADUATED, EXPELLED, TRANSFERRED }

    private Long id;
    private Long userId;
    private String studentCode;
    private String name;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String email;
    private String phone;
    private String address;
    private LocalDate admissionDate;
    private Long classId;
    private Long sectionId;
    private Long parentId;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Transient fields for display
    private String className;
    private String sectionName;
    private String parentName;

    public Student() {}

    // ---- Getters & Setters ----
    public Long getId()                         { return id; }
    public void setId(Long id)                  { this.id = id; }
    public Long getUserId()                     { return userId; }
    public void setUserId(Long userId)          { this.userId = userId; }
    public String getStudentCode()              { return studentCode; }
    public void setStudentCode(String c)        { this.studentCode = c; }
    public String getName()                     { return name; }
    public void setName(String name)            { this.name = name; }
    public LocalDate getDateOfBirth()           { return dateOfBirth; }
    public void setDateOfBirth(LocalDate d)     { this.dateOfBirth = d; }
    public Gender getGender()                   { return gender; }
    public void setGender(Gender gender)        { this.gender = gender; }
    public String getEmail()                    { return email; }
    public void setEmail(String email)          { this.email = email; }
    public String getPhone()                    { return phone; }
    public void setPhone(String phone)          { this.phone = phone; }
    public String getAddress()                  { return address; }
    public void setAddress(String address)      { this.address = address; }
    public LocalDate getAdmissionDate()         { return admissionDate; }
    public void setAdmissionDate(LocalDate d)   { this.admissionDate = d; }
    public Long getClassId()                    { return classId; }
    public void setClassId(Long classId)        { this.classId = classId; }
    public Long getSectionId()                  { return sectionId; }
    public void setSectionId(Long sectionId)    { this.sectionId = sectionId; }
    public Long getParentId()                   { return parentId; }
    public void setParentId(Long parentId)      { this.parentId = parentId; }
    public Status getStatus()                   { return status; }
    public void setStatus(Status status)        { this.status = status; }
    public LocalDateTime getCreatedAt()         { return createdAt; }
    public void setCreatedAt(LocalDateTime c)   { this.createdAt = c; }
    public LocalDateTime getUpdatedAt()         { return updatedAt; }
    public void setUpdatedAt(LocalDateTime u)   { this.updatedAt = u; }
    public String getClassName()                { return className; }
    public void setClassName(String cn)         { this.className = cn; }
    public String getSectionName()              { return sectionName; }
    public void setSectionName(String sn)       { this.sectionName = sn; }
    public String getParentName()               { return parentName; }
    public void setParentName(String pn)        { this.parentName = pn; }

    @Override
    public String toString() {
        return "Student{id=" + id + ", code='" + studentCode + "', name='" + name + "'}";
    }
}
