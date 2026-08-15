package com.smartschool.dao;

import com.smartschool.model.AuditLog;
import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogDao {
    void log(AuditLog auditLog);
    List<AuditLog> findByUserId(Long userId);
    List<AuditLog> findByEntityType(String entityType);
    List<AuditLog> findByDateRange(LocalDateTime from, LocalDateTime to);
    List<AuditLog> findAll(int limit, int offset);
    long count();
}
