package com.smartschool.dao.impl;

import com.smartschool.config.DatabaseConnectionManager;
import com.smartschool.dao.UserDao;
import com.smartschool.exception.DatabaseException;
import com.smartschool.model.Role;
import com.smartschool.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of UserDao.
 * All queries use PreparedStatements — no SQL injection possible.
 * Uses try-with-resources for automatic resource cleanup.
 */
public class UserDaoImpl implements UserDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    private Connection getConnection() throws SQLException {
        return DatabaseConnectionManager.getInstance().getConnection();
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setUsername(rs.getString("username"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRole(Role.valueOf(rs.getString("role")));
        u.setActive(rs.getBoolean("is_active"));
        u.setFailedAttempts(rs.getInt("failed_attempts"));
        Timestamp lockedUntil = rs.getTimestamp("locked_until");
        if (lockedUntil != null) u.setLockedUntil(lockedUntil.toLocalDateTime());
        Timestamp lastLogin = rs.getTimestamp("last_login");
        if (lastLogin != null) u.setLastLogin(lastLogin.toLocalDateTime());
        u.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        u.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return u;
    }

    @Override
    public User save(User user) {
        String sql = "INSERT INTO users (username, email, password_hash, role, is_active) VALUES (?,?,?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getRole().name());
            ps.setBoolean(5, user.isActive());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) user.setId(keys.getLong(1));
            }
            logger.info("User saved: {} (role={})", user.getUsername(), user.getRole());
            return user;
        } catch (SQLException e) {
            logger.error("Failed to save user: {}", e.getMessage());
            throw new DatabaseException("Failed to save user", e);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find user by id", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find user by username", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find user by email", e);
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) users.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch all users", e);
        }
        return users;
    }

    @Override
    public List<User> findByRole(Role role) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) users.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch users by role", e);
        }
        return users;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET email=?, is_active=?, role=? WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getEmail());
            ps.setBoolean(2, user.isActive());
            ps.setString(3, user.getRole().name());
            ps.setLong(4, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update user", e);
        }
        return user;
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete user", e);
        }
    }

    @Override
    public boolean updatePassword(Long userId, String newPasswordHash) {
        String sql = "UPDATE users SET password_hash = ?, failed_attempts = 0, locked_until = NULL WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPasswordHash);
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update password", e);
        }
    }

    @Override
    public boolean incrementFailedAttempts(Long userId) {
        String sql = "UPDATE users SET failed_attempts = failed_attempts + 1 WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to increment failed attempts", e);
        }
    }

    @Override
    public boolean lockAccount(Long userId, LocalDateTime until) {
        String sql = "UPDATE users SET locked_until = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(until));
            ps.setLong(2, userId);
            logger.warn("Account locked for user id={} until {}", userId, until);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to lock account", e);
        }
    }

    @Override
    public boolean unlockAccount(Long userId) {
        String sql = "UPDATE users SET locked_until = NULL, failed_attempts = 0 WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to unlock account", e);
        }
    }

    @Override
    public boolean updateLastLogin(Long userId) {
        String sql = "UPDATE users SET last_login = NOW(), failed_attempts = 0 WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update last login", e);
        }
    }

    @Override
    public boolean setActive(Long userId, boolean active) {
        String sql = "UPDATE users SET is_active = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, active);
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to set active status", e);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count users", e);
        }
        return 0;
    }
}
