package com.smartschool.model;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class Timetable {
    public enum Day { MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY }

    private Long id;
    private Long classId;
    private Long sectionId;
    private Long subjectId;
    private Long teacherId;
    private Day dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private String roomNumber;
    private boolean active;
    private LocalDateTime createdAt;

    // Transient
    private String className;
    private String sectionName;
    private String subjectName;
    private String teacherName;

    public Timetable() {}

    public Long getId()                         { return id; }
    public void setId(Long id)                  { this.id = id; }
    public Long getClassId()                    { return classId; }
    public void setClassId(Long classId)        { this.classId = classId; }
    public Long getSectionId()                  { return sectionId; }
    public void setSectionId(Long sectionId)    { this.sectionId = sectionId; }
    public Long getSubjectId()                  { return subjectId; }
    public void setSubjectId(Long subjectId)    { this.subjectId = subjectId; }
    public Long getTeacherId()                  { return teacherId; }
    public void setTeacherId(Long teacherId)    { this.teacherId = teacherId; }
    public Day getDayOfWeek()                   { return dayOfWeek; }
    public void setDayOfWeek(Day dayOfWeek)     { this.dayOfWeek = dayOfWeek; }
    public LocalTime getStartTime()             { return startTime; }
    public void setStartTime(LocalTime t)       { this.startTime = t; }
    public LocalTime getEndTime()               { return endTime; }
    public void setEndTime(LocalTime t)         { this.endTime = t; }
    public String getRoomNumber()               { return roomNumber; }
    public void setRoomNumber(String r)         { this.roomNumber = r; }
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
    public String getTeacherName()              { return teacherName; }
    public void setTeacherName(String tn)       { this.teacherName = tn; }
}
