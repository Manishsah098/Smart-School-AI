package com.smartschool.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Attendance {
    public enum Status { PRESENT, ABSENT, LATE, EXCUSED }

    private Long id;
    private Long studentId;
    private Long subjectId;
    private Long classId;
    private Long sectionId;
    private Long teacherId;
    private LocalDate attendanceDate;
    private Status status;
    private String remarks;
    private LocalDateTime markedAt;

    // Transient
    private String studentName;
    private String subjectName;

    public Attendance() {}

    public Long getId()                         { return id; }
    public void setId(Long id)                  { this.id = id; }
    public Long getStudentId()                  { return studentId; }
    public void setStudentId(Long sid)          { this.studentId = sid; }
    public Long getSubjectId()                  { return subjectId; }
    public void setSubjectId(Long sid)          { this.subjectId = sid; }
    public Long getClassId()                    { return classId; }
    public void setClassId(Long classId)        { this.classId = classId; }
    public Long getSectionId()                  { return sectionId; }
    public void setSectionId(Long sectionId)    { this.sectionId = sectionId; }
    public Long getTeacherId()                  { return teacherId; }
    public void setTeacherId(Long teacherId)    { this.teacherId = teacherId; }
    public LocalDate getAttendanceDate()        { return attendanceDate; }
    public void setAttendanceDate(LocalDate d)  { this.attendanceDate = d; }
    public Status getStatus()                   { return status; }
    public void setStatus(Status status)        { this.status = status; }
    public String getRemarks()                  { return remarks; }
    public void setRemarks(String remarks)      { this.remarks = remarks; }
    public LocalDateTime getMarkedAt()          { return markedAt; }
    public void setMarkedAt(LocalDateTime m)    { this.markedAt = m; }
    public String getStudentName()              { return studentName; }
    public void setStudentName(String n)        { this.studentName = n; }
    public String getSubjectName()              { return subjectName; }
    public void setSubjectName(String n)        { this.subjectName = n; }
}
