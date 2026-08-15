package com.smartschool.security;

import com.smartschool.model.User;

/**
 * Thread-local session holder.
 * Stores the currently authenticated user for the duration of a request/action.
 * Allows role-based permission checks anywhere in the stack without
 * passing the user object explicitly through every method signature.
 */
public final class SessionContext {
    private static final ThreadLocal<User> currentUser = new ThreadLocal<>();

    private SessionContext() {}

    public static void setCurrentUser(User user) {
        currentUser.set(user);
    }

    public static User getCurrentUser() {
        return currentUser.get();
    }

    public static void clear() {
        currentUser.remove();
    }

    public static boolean isAuthenticated() {
        return currentUser.get() != null;
    }

    public static boolean hasRole(com.smartschool.model.Role role) {
        User u = currentUser.get();
        return u != null && u.getRole() == role;
    }
}
