package com.smartschool.security;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility for BCrypt password hashing and verification.
 * NEVER store plaintext passwords. BCrypt uses salted hashing.
 */
public final class PasswordHasher {
    private static final Logger logger = LoggerFactory.getLogger(PasswordHasher.class);
    private static final int WORK_FACTOR = 12; // bcrypt cost factor

    private PasswordHasher() {}

    /** Returns a BCrypt hash of the given plaintext password */
    public static String hash(String plaintext) {
        if (plaintext == null || plaintext.isBlank()) {
            throw new IllegalArgumentException("Password cannot be blank");
        }
        return BCrypt.hashpw(plaintext, BCrypt.gensalt(WORK_FACTOR));
    }

    /** Verifies a plaintext password against a stored hash */
    public static boolean verify(String plaintext, String hash) {
        if (plaintext == null || hash == null) return false;
        try {
            return BCrypt.checkpw(plaintext, hash);
        } catch (Exception e) {
            logger.error("Password verification error (bad hash format?)", e);
            return false;
        }
    }
}
