package com.smartschool.model;

/**
 * Represents the role of a user in the system.
 * Each role has a different set of permissions enforced by RoleGuard.
 */
public enum Role {
    ADMIN,
    TEACHER,
    STUDENT,
    PARENT,
    ACCOUNTANT
}
