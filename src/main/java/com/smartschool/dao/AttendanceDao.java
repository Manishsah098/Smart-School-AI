package com.smartschool.dao;

import com.smartschool.model.Attendance;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AttendanceDao extends GenericDao<Attendance, Long> {
    List<Attendance> findByStudentId(Long studentId);
    List<Attendance> findByStudentAndDateRange(Long studentId, LocalDate from, LocalDate to);
    List<Attendance> findByClassAndDate(Long classId, Long sectionId, Long subjectId, LocalDate date);
    Optional<Attendance> findByStudentSubjectDate(Long studentId, Long subjectId, LocalDate date);
    boolean saveOrUpdate(Attendance attendance);

    /** Returns attendance percentage 0-100 for a student in a subject */
    double calculateAttendancePercentage(Long studentId, Long subjectId);

    /** Returns overall attendance percentage across all subjects */
    double calculateOverallAttendancePercentage(Long studentId);

    /** Returns weekly percentages for the last N weeks (key=weekLabel, value=%) */
    Map<String, Double> getWeeklyAttendanceTrend(Long studentId, int weeks);

    long countByStudentAndStatus(Long studentId, Attendance.Status status);
}
