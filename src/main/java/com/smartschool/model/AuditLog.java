package com.smartschool.model;

import java.time.LocalDateTime;

public class AuditLog {
    private Long id;
    private Long userId;
    private String action;
    private String entityType;
    private Long entityId;
    private String oldValue;
    private String newValue;
    private String ipAddress;
    private String description;
    private LocalDateTime createdAt;

    public AuditLog() {}

    public AuditLog(Long userId, String action, String entityType, Long entityId, String description) {
        this.userId = userId;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId()                         { return id; }
    public void setId(Long id)                  { this.id = id; }
    public Long getUserId()                     { return userId; }
    public void setUserId(Long userId)          { this.userId = userId; }
    public String getAction()                   { return action; }
    public void setAction(String action)        { this.action = action; }
    public String getEntityType()               { return entityType; }
    public void setEntityType(String et)        { this.entityType = et; }
    public Long getEntityId()                   { return entityId; }
    public void setEntityId(Long entityId)      { this.entityId = entityId; }
    public String getOldValue()                 { return oldValue; }
    public void setOldValue(String oldValue)    { this.oldValue = oldValue; }
    public String getNewValue()                 { return newValue; }
    public void setNewValue(String newValue)    { this.newValue = newValue; }
    public String getIpAddress()                { return ipAddress; }
    public void setIpAddress(String ip)         { this.ipAddress = ip; }
    public String getDescription()              { return description; }
    public void setDescription(String d)        { this.description = d; }
    public LocalDateTime getCreatedAt()         { return createdAt; }
    public void setCreatedAt(LocalDateTime c)   { this.createdAt = c; }
}
