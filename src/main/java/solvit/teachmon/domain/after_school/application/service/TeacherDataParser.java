package solvit.teachmon.domain.after_school.application.service;

import solvit.teachmon.domain.after_school.exception.TeacherDataFormatException;

public class TeacherDataParser {
    
    public record TeacherInfo(String name, String email) {}
    
    public static TeacherInfo parse(String teacherData, long rowNumber) {
        String trimmed = teacherData.trim();
        
        if (!trimmed.contains("(") || !trimmed.contains(")")) {
            throw new TeacherDataFormatException(rowNumber, teacherData);
        }
        
        int openParen = trimmed.indexOf('(');
        int closeParen = trimmed.indexOf(')', openParen);
        
        if (openParen == -1 || closeParen == -1 || closeParen <= openParen + 1) {
            throw new TeacherDataFormatException(rowNumber, teacherData);
        }
        
        String name = trimmed.substring(0, openParen).trim();
        String email = trimmed.substring(openParen + 1, closeParen).trim();
        
        if (name.isEmpty() || email.isEmpty()) {
            throw new TeacherDataFormatException(rowNumber, teacherData);
        }
        
        return new TeacherInfo(name, email);
    }
}
