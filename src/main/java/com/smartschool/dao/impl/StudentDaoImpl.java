package com.smartschool.dao.impl;

import com.smartschool.config.DatabaseConnectionManager;
import com.smartschool.dao.StudentDao;
import com.smartschool.exception.DatabaseException;
import com.smartschool.model.Gender;
import com.smartschool.model.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentDaoImpl implements StudentDao {
    private static final Logger logger = LoggerFactory.getLogger(StudentDaoImpl.class);

    private Connection getConnection() throws SQLException {
        return DatabaseConnectionManager.getInstance().getConnection();
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getLong("id"));
        s.setUserId(rs.getLong("user_id"));
        s.setStudentCode(rs.getString("student_code"));
        s.setName(rs.getString("name"));
        Date dob = rs.getDate("date_of_birth");
        if (dob != null) s.setDateOfBirth(dob.toLocalDate());
        s.setGender(Gender.valueOf(rs.getString("gender")));
        s.setEmail(rs.getString("email"));
        s.setPhone(rs.getString("phone"));
        s.setAddress(rs.getString("address"));
        Date admDate = rs.getDate("admission_date");
        if (admDate != null) s.setAdmissionDate(admDate.toLocalDate());
        long classId = rs.getLong("class_id");
        if (!rs.wasNull()) s.setClassId(classId);
        long sectionId = rs.getLong("section_id");
        if (!rs.wasNull()) s.setSectionId(sectionId);
        long parentId = rs.getLong("parent_id");
        if (!rs.wasNull()) s.setParentId(parentId);
        s.setStatus(Student.Status.valueOf(rs.getString("status")));
        s.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        s.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        // Transient fields - may come from JOINs
        try { s.setClassName(rs.getString("class_name")); } catch (SQLException ignored) {}
        try { s.setSectionName(rs.getString("section_name")); } catch (SQLException ignored) {}
        return s;
    }

    @Override
    public Student save(Student student) {
        String sql = """
            INSERT INTO students (user_id, student_code, name, date_of_birth, gender, email,
                phone, address, admission_date, class_id, section_id, parent_id, status)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)
            """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, student.getUserId());
            ps.setString(2, student.getStudentCode());
            ps.setString(3, student.getName());
            ps.setDate(4, Date.valueOf(student.getDateOfBirth()));
            ps.setString(5, student.getGender().name());
            ps.setString(6, student.getEmail());
            ps.setString(7, student.getPhone());
            ps.setString(8, student.getAddress());
            ps.setDate(9, Date.valueOf(student.getAdmissionDate()));
            if (student.getClassId() != null) ps.setLong(10, student.getClassId()); else ps.setNull(10, Types.BIGINT);
            if (student.getSectionId() != null) ps.setLong(11, student.getSectionId()); else ps.setNull(11, Types.BIGINT);
            if (student.getParentId() != null) ps.setLong(12, student.getParentId()); else ps.setNull(12, Types.BIGINT);
            ps.setString(13, student.getStatus().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) student.setId(keys.getLong(1));
            }
            logger.info("Student saved: {} ({})", student.getName(), student.getStudentCode());
            return student;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save student", e);
        }
    }

    @Override
    public Optional<Student> findById(Long id) {
        String sql = """
            SELECT s.*, c.name AS class_name, sec.name AS section_name
            FROM students s
            LEFT JOIN classes c ON s.class_id = c.id
            LEFT JOIN sections sec ON s.section_id = sec.id
            WHERE s.id = ?
            """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find student by id", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Student> findByStudentCode(String studentCode) {
        String sql = "SELECT s.*, c.name AS class_name, sec.name AS section_name FROM students s " +
                     "LEFT JOIN classes c ON s.class_id = c.id LEFT JOIN sections sec ON s.section_id = sec.id " +
                     "WHERE s.student_code = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find student by code", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Student> findAll() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT s.*, c.name AS class_name, sec.name AS section_name FROM students s " +
                     "LEFT JOIN classes c ON s.class_id = c.id LEFT JOIN sections sec ON s.section_id = sec.id " +
                     "ORDER BY s.name";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch all students", e);
        }
        return list;
    }

    @Override
    public List<Student> findByClassId(Long classId) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT s.*, c.name AS class_name, sec.name AS section_name FROM students s " +
                     "LEFT JOIN classes c ON s.class_id = c.id LEFT JOIN sections sec ON s.section_id = sec.id " +
                     "WHERE s.class_id = ? AND s.status = 'ACTIVE' ORDER BY s.name";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, classId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch students by class", e);
        }
        return list;
    }

    @Override
    public List<Student> findBySectionId(Long sectionId) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT s.*, c.name AS class_name, sec.name AS section_name FROM students s " +
                     "LEFT JOIN classes c ON s.class_id = c.id LEFT JOIN sections sec ON s.section_id = sec.id " +
                     "WHERE s.section_id = ? AND s.status = 'ACTIVE' ORDER BY s.name";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, sectionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch students by section", e);
        }
        return list;
    }

    @Override
    public List<Student> findByParentId(Long parentId) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT s.*, c.name AS class_name, sec.name AS section_name FROM students s " +
                     "LEFT JOIN classes c ON s.class_id = c.id LEFT JOIN sections sec ON s.section_id = sec.id " +
                     "WHERE s.parent_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, parentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch students by parent", e);
        }
        return list;
    }

    @Override
    public List<Student> findByStatus(Student.Status status) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE status = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch students by status", e);
        }
        return list;
    }

    @Override
    public List<Student> searchByName(String name) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT s.*, c.name AS class_name, sec.name AS section_name FROM students s " +
                     "LEFT JOIN classes c ON s.class_id = c.id LEFT JOIN sections sec ON s.section_id = sec.id " +
                     "WHERE s.name LIKE ? ORDER BY s.name";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + name + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to search students", e);
        }
        return list;
    }

    @Override
    public Student update(Student student) {
        String sql = """
            UPDATE students SET name=?, date_of_birth=?, gender=?, email=?,
                phone=?, address=?, class_id=?, section_id=?, parent_id=?, status=?
            WHERE id=?
            """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, student.getName());
            ps.setDate(2, Date.valueOf(student.getDateOfBirth()));
            ps.setString(3, student.getGender().name());
            ps.setString(4, student.getEmail());
            ps.setString(5, student.getPhone());
            ps.setString(6, student.getAddress());
            if (student.getClassId() != null) ps.setLong(7, student.getClassId()); else ps.setNull(7, Types.BIGINT);
            if (student.getSectionId() != null) ps.setLong(8, student.getSectionId()); else ps.setNull(8, Types.BIGINT);
            if (student.getParentId() != null) ps.setLong(9, student.getParentId()); else ps.setNull(9, Types.BIGINT);
            ps.setString(10, student.getStatus().name());
            ps.setLong(11, student.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update student", e);
        }
        return student;
    }

    @Override
    public boolean delete(Long id) {
        // Soft delete: deactivate instead of physical delete
        String sql = "UPDATE students SET status = 'INACTIVE' WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to deactivate student", e);
        }
    }

    @Override
    public boolean assignToClass(Long studentId, Long classId, Long sectionId) {
        String sql = "UPDATE students SET class_id = ?, section_id = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, classId);
            ps.setLong(2, sectionId);
            ps.setLong(3, studentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to assign student to class", e);
        }
    }

    @Override
    public boolean assignParent(Long studentId, Long parentId) {
        String sql = "UPDATE students SET parent_id = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, parentId);
            ps.setLong(2, studentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to assign parent to student", e);
        }
    }

    @Override
    public String generateStudentCode() {
        int year = java.time.Year.now().getValue();
        String sql = "SELECT COUNT(*) FROM students WHERE student_code LIKE ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "SS" + year + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1) + 1;
                    return String.format("SS%d%04d", year, count);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to generate student code", e);
        }
        return "SS" + year + "0001";
    }

    @Override
    public long countByClassId(Long classId) {
        String sql = "SELECT COUNT(*) FROM students WHERE class_id = ? AND status = 'ACTIVE'";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, classId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count students in class", e);
        }
        return 0;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM students WHERE status = 'ACTIVE'";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count students", e);
        }
        return 0;
    }
}
