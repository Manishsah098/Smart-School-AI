package com.smartschool.dao;

import com.smartschool.model.User;
import com.smartschool.model.Role;

import java.util.List;
import java.util.Optional;

public interface UserDao extends GenericDao<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
    boolean updatePassword(Long userId, String newPasswordHash);
    boolean incrementFailedAttempts(Long userId);
    boolean lockAccount(Long userId, java.time.LocalDateTime until);
    boolean unlockAccount(Long userId);
    boolean updateLastLogin(Long userId);
    boolean setActive(Long userId, boolean active);
}
