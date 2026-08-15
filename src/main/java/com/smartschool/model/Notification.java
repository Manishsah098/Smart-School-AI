package com.smartschool.model;

import java.time.LocalDateTime;

public class Notification {
    public enum Type { ATTENDANCE_ALERT, FEE_REMINDER, EXAM_REMINDER, NOTICE, AI_RECOMMENDATION, SYSTEM }

    private Long id;
    private Long recipientId;
    private Type type;
    private String title;
    private String message;
    private boolean read;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;

    public Notification() {}

    public Long getId()                         { return id; }
    public void setId(Long id)                  { this.id = id; }
    public Long getRecipientId()                { return recipientId; }
    public void setRecipientId(Long rid)        { this.recipientId = rid; }
    public Type getType()                       { return type; }
    public void setType(Type type)              { this.type = type; }
    public String getTitle()                    { return title; }
    public void setTitle(String title)          { this.title = title; }
    public String getMessage()                  { return message; }
    public void setMessage(String message)      { this.message = message; }
    public boolean isRead()                     { return read; }
    public void setRead(boolean read)           { this.read = read; }
    public LocalDateTime getReadAt()            { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
    public LocalDateTime getCreatedAt()         { return createdAt; }
    public void setCreatedAt(LocalDateTime c)   { this.createdAt = c; }
}
