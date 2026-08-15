package com.smartschool.security;

import com.smartschool.exception.AuthorizationException;
import com.smartschool.model.Role;
import com.smartschool.model.User;

/**
 * Enforces Role-Based Access Control (RBAC).
 * Throw AuthorizationException if the current user lacks required permissions.
 * AI agents call this before any write operations.
 */
public final class RoleGuard {
    private RoleGuard() {}

    /** Throws AuthorizationException if no user is in session */
    public static void requireAuthenticated() {
        if (!SessionContext.isAuthenticated()) {
            throw new AuthorizationException("Authentication required. Please log in.");
        }
    }

    /** Throws AuthorizationException if current user does not have required role */
    public static void requireRole(Role... allowedRoles) {
        requireAuthenticated();
        User current = SessionContext.getCurrentUser();
        for (Role role : allowedRoles) {
            if (current.getRole() == role) return;
        }
        throw new AuthorizationException(
            "Access denied. Required roles: " + java.util.Arrays.toString(allowedRoles) +
            ", but current role is: " + current.getRole()
        );
    }

    public static void requireAdmin() {
        requireRole(Role.ADMIN);
    }

    public static void requireTeacherOrAdmin() {
        requireRole(Role.TEACHER, Role.ADMIN);
    }

    public static void requireAccountantOrAdmin() {
        requireRole(Role.ACCOUNTANT, Role.ADMIN);
    }

    /** Check if current user has access to a student's data (admin, teacher, the student's parent, or the student) */
    public static void requireStudentDataAccess(Long targetStudentUserId) {
        requireAuthenticated();
        User current = SessionContext.getCurrentUser();
        if (current.getRole() == Role.ADMIN || current.getRole() == Role.TEACHER) return;
        if (current.getRole() == Role.STUDENT && current.getId().equals(targetStudentUserId)) return;
        // Parent access checked at service layer
        if (current.getRole() == Role.PARENT) return;
        throw new AuthorizationException("Access denied to student data.");
    }
}
