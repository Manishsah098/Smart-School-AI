package com.smartschool.dao;

import com.smartschool.model.Marks;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface MarksDao extends GenericDao<Marks, Long> {
    Optional<Marks> findByStudentAndExam(Long studentId, Long examId);
    List<Marks> findByStudentId(Long studentId);
    List<Marks> findByExamId(Long examId);
    List<Marks> findByStudentAndSubject(Long studentId, Long subjectId);
    BigDecimal calculateStudentAverage(Long studentId);
    BigDecimal calculateExamAverage(Long examId);
    List<Marks> findTopPerformers(Long examId, int limit);
    List<Marks> findAtRiskStudents(Long examId, double threshold);
    int calculateRank(Long studentId, Long examId);
}
