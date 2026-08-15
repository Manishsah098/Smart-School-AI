package com.smartschool.dao;

import com.smartschool.model.Fee;
import com.smartschool.model.Payment;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface FeeDao extends GenericDao<Fee, Long> {
    List<Fee> findByStudentId(Long studentId);
    List<Fee> findByStatus(Fee.Status status);
    List<Fee> findOverdueFees();
    Optional<Fee> findByStudentAndStructure(Long studentId, Long feeStructureId);

    /**
     * Executes the full payment transaction atomically:
     * 1. Insert payment record
     * 2. Update fee paid_amount & status
     * 3. COMMIT or ROLLBACK on failure
     */
    Payment recordPayment(Payment payment) throws Exception;

    List<Payment> findPaymentsByStudentId(Long studentId);
    List<Payment> findAllPayments();
    BigDecimal getTotalCollectedAmount();
    BigDecimal getTotalPendingAmount();
    long countByStatus(Fee.Status status);
}
