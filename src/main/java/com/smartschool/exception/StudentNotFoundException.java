package com.smartschool.exception;

public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(String message) { super(message); }
    public StudentNotFoundException(Long id) { super("Student not found with id: " + id); }
}
