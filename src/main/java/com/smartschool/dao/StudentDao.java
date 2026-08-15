package com.smartschool.dao;

import com.smartschool.model.Student;
import java.util.List;
import java.util.Optional;

public interface StudentDao extends GenericDao<Student, Long> {
    Optional<Student> findByStudentCode(String studentCode);
    List<Student> findByClassId(Long classId);
    List<Student> findBySectionId(Long sectionId);
    List<Student> findByParentId(Long parentId);
    List<Student> findByStatus(Student.Status status);
    List<Student> searchByName(String name);
    boolean assignToClass(Long studentId, Long classId, Long sectionId);
    boolean assignParent(Long studentId, Long parentId);
    String generateStudentCode();
    long countByClassId(Long classId);
}
