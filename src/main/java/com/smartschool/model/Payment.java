package com.smartschool.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {
    public enum Mode { CASH, CHEQUE, ONLINE, CARD }

    private Long id;
    private Long feeId;
    private Long studentId;
    private BigDecimal amountPaid;
    private LocalDateTime paymentDate;
    private Mode paymentMode;
    private String receiptNumber;
    private String transactionRef;
    private Long collectedBy;
    private String remarks;

    // Transient
    private String studentName;
    private String collectorName;

    public Payment() {}

    public Long getId()                         { return id; }
    public void setId(Long id)                  { this.id = id; }
    public Long getFeeId()                      { return feeId; }
    public void setFeeId(Long feeId)            { this.feeId = feeId; }
    public Long getStudentId()                  { return studentId; }
    public void setStudentId(Long sid)          { this.studentId = sid; }
    public BigDecimal getAmountPaid()           { return amountPaid; }
    public void setAmountPaid(BigDecimal a)     { this.amountPaid = a; }
    public LocalDateTime getPaymentDate()       { return paymentDate; }
    public void setPaymentDate(LocalDateTime p) { this.paymentDate = p; }
    public Mode getPaymentMode()                { return paymentMode; }
    public void setPaymentMode(Mode m)          { this.paymentMode = m; }
    public String getReceiptNumber()            { return receiptNumber; }
    public void setReceiptNumber(String r)      { this.receiptNumber = r; }
    public String getTransactionRef()           { return transactionRef; }
    public void setTransactionRef(String t)     { this.transactionRef = t; }
    public Long getCollectedBy()                { return collectedBy; }
    public void setCollectedBy(Long c)          { this.collectedBy = c; }
    public String getRemarks()                  { return remarks; }
    public void setRemarks(String remarks)      { this.remarks = remarks; }
    public String getStudentName()              { return studentName; }
    public void setStudentName(String n)        { this.studentName = n; }
    public String getCollectorName()            { return collectorName; }
    public void setCollectorName(String n)      { this.collectorName = n; }
}
