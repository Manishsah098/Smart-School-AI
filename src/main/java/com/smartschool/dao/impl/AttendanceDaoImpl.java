package com.smartschool.dao.impl;

import com.smartschool.config.DatabaseConnectionManager;
import com.smartschool.dao.AttendanceDao;
import com.smartschool.exception.DatabaseException;
import com.smartschool.model.Attendance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class AttendanceDaoImpl implements AttendanceDao {
    private static final Logger logger = LoggerFactory.getLogger(AttendanceDaoImpl.class);

    private Connection getConnection() throws SQLException {
        return DatabaseConnectionManager.getInstance().getConnection();
    }

    private Attendance mapRow(ResultSet rs) throws SQLException {
        Attendance a = new Attendance();
        a.setId(rs.getLong("id"));
        a.setStudentId(rs.getLong("student_id"));
        a.setSubjectId(rs.getLong("subject_id"));
        a.setClassId(rs.getLong("class_id"));
        a.setSectionId(rs.getLong("section_id"));
        a.setTeacherId(rs.getLong("teacher_id"));
        a.setAttendanceDate(rs.getDate("attendance_date").toLocalDate());
        a.setStatus(Attendance.Status.valueOf(rs.getString("status")));
        a.setRemarks(rs.getString("remarks"));
        a.setMarkedAt(rs.getTimestamp("marked_at").toLocalDateTime());
        try { a.setStudentName(rs.getString("student_name")); } catch (SQLException ignored) {}
        return a;
    }

    @Override
    public Attendance save(Attendance att) {
        String sql = "INSERT INTO attendance (student_id, subject_id, class_id, section_id, teacher_id, " +
                     "attendance_date, status, remarks) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, att.getStudentId());
            ps.setLong(2, att.getSubjectId());
            ps.setLong(3, att.getClassId());
            ps.setLong(4, att.getSectionId());
            ps.setLong(5, att.getTeacherId());
            ps.setDate(6, Date.valueOf(att.getAttendanceDate()));
            ps.setString(7, att.getStatus().name());
            ps.setString(8, att.getRemarks());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) att.setId(keys.getLong(1));
            }
            return att;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save attendance", e);
        }
    }

    @Override
    public boolean saveOrUpdate(Attendance att) {
        String sql = """
            INSERT INTO attendance (student_id, subject_id, class_id, section_id, teacher_id,
                attendance_date, status, remarks)
            VALUES (?,?,?,?,?,?,?,?)
            ON DUPLICATE KEY UPDATE status=VALUES(status), remarks=VALUES(remarks)
            """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, att.getStudentId());
            ps.setLong(2, att.getSubjectId());
            ps.setLong(3, att.getClassId());
            ps.setLong(4, att.getSectionId());
            ps.setLong(5, att.getTeacherId());
            ps.setDate(6, Date.valueOf(att.getAttendanceDate()));
            ps.setString(7, att.getStatus().name());
            ps.setString(8, att.getRemarks());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save/update attendance", e);
        }
    }

    @Override
    public Optional<Attendance> findById(Long id) {
        String sql = "SELECT * FROM attendance WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find attendance", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Attendance> findAll() {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT a.*, s.name AS student_name FROM attendance a JOIN students s ON a.student_id = s.id ORDER BY a.attendance_date DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch attendance", e);
        }
        return list;
    }

    @Override
    public List<Attendance> findByStudentId(Long studentId) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT * FROM attendance WHERE student_id = ? ORDER BY attendance_date DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch attendance by student", e);
        }
        return list;
    }

    @Override
    public List<Attendance> findByStudentAndDateRange(Long studentId, LocalDate from, LocalDate to) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT * FROM attendance WHERE student_id = ? AND attendance_date BETWEEN ? AND ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ps.setDate(2, Date.valueOf(from));
            ps.setDate(3, Date.valueOf(to));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch attendance by date range", e);
        }
        return list;
    }

    @Override
    public List<Attendance> findByClassAndDate(Long classId, Long sectionId, Long subjectId, LocalDate date) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT a.*, s.name AS student_name FROM attendance a JOIN students s ON a.student_id = s.id " +
                     "WHERE a.class_id = ? AND a.section_id = ? AND a.subject_id = ? AND a.attendance_date = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, classId);
            ps.setLong(2, sectionId);
            ps.setLong(3, subjectId);
            ps.setDate(4, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch class attendance", e);
        }
        return list;
    }

    @Override
    public Optional<Attendance> findByStudentSubjectDate(Long studentId, Long subjectId, LocalDate date) {
        String sql = "SELECT * FROM attendance WHERE student_id = ? AND subject_id = ? AND attendance_date = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ps.setLong(2, subjectId);
            ps.setDate(3, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find attendance record", e);
        }
        return Optional.empty();
    }

    @Override
    public double calculateAttendancePercentage(Long studentId, Long subjectId) {
        String sql = "SELECT " +
                     "COUNT(*) AS total, " +
                     "SUM(CASE WHEN status IN ('PRESENT','LATE') THEN 1 ELSE 0 END) AS present " +
                     "FROM attendance WHERE student_id = ? AND subject_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ps.setLong(2, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total");
                    int present = rs.getInt("present");
                    if (total == 0) return 100.0;
                    return (double) present / total * 100.0;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to calculate attendance percentage", e);
        }
        return 0.0;
    }

    @Override
    public double calculateOverallAttendancePercentage(Long studentId) {
        String sql = "SELECT " +
                     "COUNT(*) AS total, " +
                     "SUM(CASE WHEN status IN ('PRESENT','LATE') THEN 1 ELSE 0 END) AS present " +
                     "FROM attendance WHERE student_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total");
                    int present = rs.getInt("present");
                    if (total == 0) return 100.0;
                    return (double) present / total * 100.0;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to calculate overall attendance", e);
        }
        return 0.0;
    }

    @Override
    public Map<String, Double> getWeeklyAttendanceTrend(Long studentId, int weeks) {
        Map<String, Double> trend = new LinkedHashMap<>();
        String sql = """
            SELECT YEARWEEK(attendance_date, 1) AS week_key,
                   MIN(attendance_date) AS week_start,
                   COUNT(*) AS total,
                   SUM(CASE WHEN status IN ('PRESENT','LATE') THEN 1 ELSE 0 END) AS present
            FROM attendance
            WHERE student_id = ? AND attendance_date >= DATE_SUB(CURDATE(), INTERVAL ? WEEK)
            GROUP BY week_key ORDER BY week_key ASC
            """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ps.setInt(2, weeks);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String label = "Week " + rs.getString("week_start");
                    int total = rs.getInt("total");
                    int present = rs.getInt("present");
                    trend.put(label, total == 0 ? 100.0 : (double) present / total * 100.0);
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to compute weekly trend", e);
        }
        return trend;
    }

    @Override
    public long countByStudentAndStatus(Long studentId, Attendance.Status status) {
        String sql = "SELECT COUNT(*) FROM attendance WHERE student_id = ? AND status = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ps.setString(2, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count attendance", e);
        }
        return 0;
    }

    @Override
    public Attendance update(Attendance att) {
        String sql = "UPDATE attendance SET status=?, remarks=? WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, att.getStatus().name());
            ps.setString(2, att.getRemarks());
            ps.setLong(3, att.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update attendance", e);
        }
        return att;
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM attendance WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete attendance", e);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM attendance";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count attendance", e);
        }
        return 0;
    }
}
