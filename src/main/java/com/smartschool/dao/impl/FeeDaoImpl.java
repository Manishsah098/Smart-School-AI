package com.smartschool.dao.impl;

import com.smartschool.config.DatabaseConnectionManager;
import com.smartschool.dao.FeeDao;
import com.smartschool.exception.DatabaseException;
import com.smartschool.exception.FeePaymentException;
import com.smartschool.model.Fee;
import com.smartschool.model.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FeeDaoImpl implements FeeDao {
    private static final Logger logger = LoggerFactory.getLogger(FeeDaoImpl.class);

    private Connection getConnection() throws SQLException {
        return DatabaseConnectionManager.getInstance().getConnection();
    }

    private Fee mapFeeRow(ResultSet rs) throws SQLException {
        Fee f = new Fee();
        f.setId(rs.getLong("id"));
        f.setStudentId(rs.getLong("student_id"));
        f.setFeeStructureId(rs.getLong("fee_structure_id"));
        f.setTotalAmount(rs.getBigDecimal("total_amount"));
        f.setPaidAmount(rs.getBigDecimal("paid_amount"));
        f.setRemainingAmount(rs.getBigDecimal("remaining_amount"));
        Date dueDate = rs.getDate("due_date");
        if (dueDate != null) f.setDueDate(dueDate.toLocalDate());
        f.setStatus(Fee.Status.valueOf(rs.getString("status")));
        f.setAcademicYear(rs.getString("academic_year"));
        f.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        try { f.setStudentName(rs.getString("student_name")); } catch (SQLException ignored) {}
        return f;
    }

    private Payment mapPaymentRow(ResultSet rs) throws SQLException {
        Payment p = new Payment();
        p.setId(rs.getLong("id"));
        p.setFeeId(rs.getLong("fee_id"));
        p.setStudentId(rs.getLong("student_id"));
        p.setAmountPaid(rs.getBigDecimal("amount_paid"));
        Timestamp payDate = rs.getTimestamp("payment_date");
        if (payDate != null) p.setPaymentDate(payDate.toLocalDateTime());
        p.setPaymentMode(Payment.Mode.valueOf(rs.getString("payment_mode")));
        p.setReceiptNumber(rs.getString("receipt_number"));
        p.setTransactionRef(rs.getString("transaction_ref"));
        p.setCollectedBy(rs.getLong("collected_by"));
        p.setRemarks(rs.getString("remarks"));
        return p;
    }

    @Override
    public Fee save(Fee fee) {
        String sql = "INSERT INTO fees (student_id, fee_structure_id, total_amount, paid_amount, due_date, status, academic_year) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, fee.getStudentId());
            ps.setLong(2, fee.getFeeStructureId());
            ps.setBigDecimal(3, fee.getTotalAmount());
            ps.setBigDecimal(4, fee.getPaidAmount() != null ? fee.getPaidAmount() : BigDecimal.ZERO);
            ps.setDate(5, Date.valueOf(fee.getDueDate()));
            ps.setString(6, fee.getStatus().name());
            ps.setString(7, fee.getAcademicYear());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) fee.setId(keys.getLong(1));
            }
            return fee;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save fee", e);
        }
    }

    /**
     * CRITICAL: Transactional payment recording.
     * Atomically inserts payment + updates fee balance.
     * COMMIT on success, ROLLBACK on any failure.
     */
    @Override
    public Payment recordPayment(Payment payment) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // BEGIN TRANSACTION

            // 1. Fetch current fee
            Fee fee = findById(payment.getFeeId()).orElseThrow(
                () -> new FeePaymentException("Fee record not found: " + payment.getFeeId())
            );

            // 2. Validate payment amount
            BigDecimal remaining = fee.getRemainingAmount();
            if (payment.getAmountPaid().compareTo(BigDecimal.ZERO) <= 0) {
                throw new FeePaymentException("Payment amount must be greater than zero.");
            }
            if (payment.getAmountPaid().compareTo(remaining) > 0) {
                throw new FeePaymentException("Payment amount (" + payment.getAmountPaid() +
                    ") exceeds remaining balance (" + remaining + ").");
            }

            // 3. Insert payment record
            String insertPayment = "INSERT INTO payments (fee_id, student_id, amount_paid, payment_mode, receipt_number, transaction_ref, collected_by, remarks) VALUES (?,?,?,?,?,?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(insertPayment, Statement.RETURN_GENERATED_KEYS)) {
                ps.setLong(1, payment.getFeeId());
                ps.setLong(2, payment.getStudentId());
                ps.setBigDecimal(3, payment.getAmountPaid());
                ps.setString(4, payment.getPaymentMode().name());
                ps.setString(5, payment.getReceiptNumber());
                ps.setString(6, payment.getTransactionRef());
                ps.setLong(7, payment.getCollectedBy());
                ps.setString(8, payment.getRemarks());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) payment.setId(keys.getLong(1));
                }
            }

            // 4. Update fee paid_amount and status
            BigDecimal newPaid = fee.getPaidAmount().add(payment.getAmountPaid());
            Fee.Status newStatus;
            if (newPaid.compareTo(fee.getTotalAmount()) >= 0) {
                newStatus = Fee.Status.PAID;
            } else {
                newStatus = Fee.Status.PARTIALLY_PAID;
            }
            String updateFee = "UPDATE fees SET paid_amount = ?, status = ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateFee)) {
                ps.setBigDecimal(1, newPaid);
                ps.setString(2, newStatus.name());
                ps.setLong(3, fee.getId());
                ps.executeUpdate();
            }

            conn.commit(); // COMMIT TRANSACTION
            logger.info("Payment recorded: receipt={}, amount={}, student={}",
                payment.getReceiptNumber(), payment.getAmountPaid(), payment.getStudentId());
            return payment;

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback(); // ROLLBACK on any failure
                    logger.error("Payment transaction rolled back: {}", e.getMessage());
                } catch (SQLException rollbackEx) {
                    logger.error("Rollback failed", rollbackEx);
                }
            }
            throw new FeePaymentException("Payment failed: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    logger.error("Failed to close connection after payment", ex);
                }
            }
        }
    }

    @Override
    public Optional<Fee> findById(Long id) {
        String sql = "SELECT f.*, s.name AS student_name FROM fees f LEFT JOIN students s ON f.student_id = s.id WHERE f.id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapFeeRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find fee by id", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Fee> findAll() {
        List<Fee> list = new ArrayList<>();
        String sql = "SELECT f.*, s.name AS student_name FROM fees f LEFT JOIN students s ON f.student_id = s.id ORDER BY f.created_at DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapFeeRow(rs));
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch fees", e);
        }
        return list;
    }

    @Override
    public List<Fee> findByStudentId(Long studentId) {
        List<Fee> list = new ArrayList<>();
        String sql = "SELECT * FROM fees WHERE student_id = ? ORDER BY due_date DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapFeeRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch fees by student", e);
        }
        return list;
    }

    @Override
    public List<Fee> findByStatus(Fee.Status status) {
        List<Fee> list = new ArrayList<>();
        String sql = "SELECT f.*, s.name AS student_name FROM fees f LEFT JOIN students s ON f.student_id = s.id WHERE f.status = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapFeeRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch fees by status", e);
        }
        return list;
    }

    @Override
    public List<Fee> findOverdueFees() {
        List<Fee> list = new ArrayList<>();
        String sql = "SELECT f.*, s.name AS student_name FROM fees f LEFT JOIN students s ON f.student_id = s.id " +
                     "WHERE f.status IN ('PENDING','PARTIALLY_PAID') AND f.due_date < CURDATE()";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapFeeRow(rs));
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch overdue fees", e);
        }
        return list;
    }

    @Override
    public Optional<Fee> findByStudentAndStructure(Long studentId, Long feeStructureId) {
        String sql = "SELECT * FROM fees WHERE student_id = ? AND fee_structure_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ps.setLong(2, feeStructureId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapFeeRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find fee by student and structure", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Payment> findPaymentsByStudentId(Long studentId) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE student_id = ? ORDER BY payment_date DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapPaymentRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch payments by student", e);
        }
        return list;
    }

    @Override
    public List<Payment> findAllPayments() {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payments ORDER BY payment_date DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapPaymentRow(rs));
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch all payments", e);
        }
        return list;
    }

    @Override
    public BigDecimal getTotalCollectedAmount() {
        String sql = "SELECT COALESCE(SUM(amount_paid), 0) FROM payments";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getBigDecimal(1);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get total collected", e);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTotalPendingAmount() {
        String sql = "SELECT COALESCE(SUM(remaining_amount), 0) FROM fees WHERE status IN ('PENDING','PARTIALLY_PAID','OVERDUE')";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getBigDecimal(1);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get total pending", e);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public long countByStatus(Fee.Status status) {
        String sql = "SELECT COUNT(*) FROM fees WHERE status = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count fees", e);
        }
        return 0;
    }

    @Override
    public Fee update(Fee fee) {
        String sql = "UPDATE fees SET total_amount=?, paid_amount=?, due_date=?, status=? WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, fee.getTotalAmount());
            ps.setBigDecimal(2, fee.getPaidAmount());
            ps.setDate(3, Date.valueOf(fee.getDueDate()));
            ps.setString(4, fee.getStatus().name());
            ps.setLong(5, fee.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update fee", e);
        }
        return fee;
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM fees WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete fee", e);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM fees";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count fees", e);
        }
        return 0;
    }
}
