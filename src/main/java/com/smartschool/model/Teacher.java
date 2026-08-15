package com.smartschool.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Teacher {
    public enum Status { ACTIVE, INACTIVE, ON_LEAVE }

    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private String qualification;
    private String department;
    private int experienceYears;
    private Status status;
    private LocalDate joinDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Teacher() {}

    public Long getId()                         { return id; }
    public void setId(Long id)                  { this.id = id; }
    public Long getUserId()                     { return userId; }
    public void setUserId(Long userId)          { this.userId = userId; }
    public String getName()                     { return name; }
    public void setName(String name)            { this.name = name; }
    public String getEmail()                    { return email; }
    public void setEmail(String email)          { this.email = email; }
    public String getPhone()                    { return phone; }
    public void setPhone(String phone)          { this.phone = phone; }
    public String getQualification()            { return qualification; }
    public void setQualification(String q)      { this.qualification = q; }
    public String getDepartment()               { return department; }
    public void setDepartment(String d)         { this.department = d; }
    public int getExperienceYears()             { return experienceYears; }
    public void setExperienceYears(int e)       { this.experienceYears = e; }
    public Status getStatus()                   { return status; }
    public void setStatus(Status status)        { this.status = status; }
    public LocalDate getJoinDate()              { return joinDate; }
    public void setJoinDate(LocalDate joinDate) { this.joinDate = joinDate; }
    public LocalDateTime getCreatedAt()         { return createdAt; }
    public void setCreatedAt(LocalDateTime c)   { this.createdAt = c; }
    public LocalDateTime getUpdatedAt()         { return updatedAt; }
    public void setUpdatedAt(LocalDateTime u)   { this.updatedAt = u; }
}
