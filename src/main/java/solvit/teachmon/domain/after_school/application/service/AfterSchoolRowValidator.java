package solvit.teachmon.domain.after_school.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import solvit.teachmon.domain.after_school.domain.enums.AfterSchoolSpreadSheetsColumn;
import solvit.teachmon.domain.after_school.exception.AfterSchoolNameNullException;
import solvit.teachmon.domain.after_school.exception.BranchNullException;
import solvit.teachmon.domain.after_school.exception.BranchRangeException;
import solvit.teachmon.domain.after_school.exception.GradeNullException;
import solvit.teachmon.domain.after_school.exception.GradeRangeException;
import solvit.teachmon.domain.after_school.exception.PeriodInvalidException;
import solvit.teachmon.domain.after_school.exception.PeriodNullException;
import solvit.teachmon.domain.after_school.exception.PlaceNotExistException;
import solvit.teachmon.domain.after_school.exception.PlaceNullException;
import solvit.teachmon.domain.after_school.exception.RowSizeMismatchException;
import solvit.teachmon.domain.after_school.exception.StudentDataFormatException;
import solvit.teachmon.domain.after_school.exception.StudentDataNullException;
import solvit.teachmon.domain.after_school.exception.StudentNotExistException;
import solvit.teachmon.domain.after_school.exception.TeacherNotExistException;
import solvit.teachmon.domain.after_school.exception.TeacherNullException;
import solvit.teachmon.domain.after_school.exception.WeekDayInvalidException;
import solvit.teachmon.domain.after_school.exception.WeekDayNullException;
import solvit.teachmon.domain.after_school.exception.YearInvalidException;
import solvit.teachmon.domain.after_school.exception.YearNullException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AfterSchoolRowValidator {

    public void validate(List<Object> row, long rowNum, ReferenceDataCache cache) {
        validateColumnCount(row, rowNum);
        validateYear(row, rowNum);
        validateBranch(row, rowNum);
        validateWeekday(row, rowNum);
        validateGrade(row, rowNum);
        validatePeriod(row, rowNum);
        validateTeacher(row, rowNum, cache);
        validatePlace(row, rowNum, cache);
        validateAfterSchoolName(row, rowNum);
        validateStudentData(row, rowNum, cache);
    }

    private void validateColumnCount(List<Object> row, long rowNum) {
        int expectedSize = AfterSchoolSpreadSheetsColumn.values().length;
        int actualSize = row.size();
        
        if (actualSize != expectedSize) {
            throw new RowSizeMismatchException(rowNum, expectedSize, actualSize);
        }
    }

    private void validateYear(List<Object> row, long rowNum) {
        Object yearObj = row.get(AfterSchoolSpreadSheetsColumn.YEAR.getIndex());
        
        if (isBlank(yearObj)) {
            throw new YearNullException(rowNum);
        }
        
        try {
            int year = Integer.parseInt(yearObj.toString().trim());
            if (year < 1000 || year > 9999) {
                throw new YearInvalidException(rowNum, yearObj.toString());
            }
        } catch (NumberFormatException e) {
            throw new YearInvalidException(rowNum, yearObj.toString());
        }
    }

    private void validateBranch(List<Object> row, long rowNum) {
        Object branchObj = row.get(AfterSchoolSpreadSheetsColumn.BRANCH.getIndex());
        
        if (isBlank(branchObj)) {
            throw new BranchNullException(rowNum);
        }
        
        try {
            int branch = Integer.parseInt(branchObj.toString().trim());
            if (branch < 1 || branch > 4) {
                throw new BranchRangeException(rowNum, branchObj.toString());
            }
        } catch (NumberFormatException e) {
            throw new BranchRangeException(rowNum, branchObj.toString());
        }
    }

    private void validateWeekday(List<Object> row, long rowNum) {
        Object weekdayObj = row.get(AfterSchoolSpreadSheetsColumn.WEEKDAY.getIndex());
        
        if (isBlank(weekdayObj)) {
            throw new WeekDayNullException(rowNum);
        }
        
        String weekdayStr = weekdayObj.toString().trim();
        boolean isValidWeekday = weekdayStr.equals("월요일") || weekdayStr.equals("화요일") ||
                weekdayStr.equals("수요일") || weekdayStr.equals("목요일");

        if (!isValidWeekday) {
            throw new WeekDayInvalidException(rowNum, weekdayStr);
        }
    }

    private void validateGrade(List<Object> row, long rowNum) {
        Object gradeObj = row.get(AfterSchoolSpreadSheetsColumn.GRADE.getIndex());
        
        if (isBlank(gradeObj)) {
            throw new GradeNullException(rowNum);
        }
        
        try {
            int grade = Integer.parseInt(gradeObj.toString().trim());
            if (grade < 1 || grade > 3) {
                throw new GradeRangeException(rowNum, gradeObj.toString());
            }
        } catch (NumberFormatException e) {
            throw new GradeRangeException(rowNum, gradeObj.toString());
        }
    }

    private void validatePeriod(List<Object> row, long rowNum) {
        Object periodObj = row.get(AfterSchoolSpreadSheetsColumn.PERIOD.getIndex());
        
        if (isBlank(periodObj)) {
            throw new PeriodNullException(rowNum);
        }
        
        String period = periodObj.toString().trim();
        if (!period.equals("8~9교시") && !period.equals("10~11교시")) {
            throw new PeriodInvalidException(rowNum, period);
        }
    }

    private void validateTeacher(List<Object> row, long rowNum, ReferenceDataCache cache) {
        Object teacherObj = row.get(AfterSchoolSpreadSheetsColumn.TEACHER.getIndex());
        
        if (isBlank(teacherObj)) {
            throw new TeacherNullException(rowNum);
        }
        
        String teacherData = teacherObj.toString().trim();

        TeacherDataParser.TeacherInfo teacherInfo = TeacherDataParser.parse(teacherData, rowNum);

        if (!cache.hasTeacher(teacherInfo.email())) {
            throw new TeacherNotExistException(rowNum, teacherInfo.email());
        }
    }

    private void validatePlace(List<Object> row, long rowNum, ReferenceDataCache cache) {
        Object placeObj = row.get(AfterSchoolSpreadSheetsColumn.PLACE.getIndex());
        
        if (isBlank(placeObj)) {
            throw new PlaceNullException(rowNum);
        }
        
        String placeName = placeObj.toString().trim();
        if (!cache.hasPlace(placeName)) {
            throw new PlaceNotExistException(rowNum, placeName);
        }
    }

    private void validateAfterSchoolName(List<Object> row, long rowNum) {
        Object nameObj = row.get(AfterSchoolSpreadSheetsColumn.NAME.getIndex());
        
        if (isBlank(nameObj)) {
            throw new AfterSchoolNameNullException(rowNum);
        }
    }

    private void validateStudentData(List<Object> row, long rowNum, ReferenceDataCache cache) {
        Object studentObj = row.get(AfterSchoolSpreadSheetsColumn.STUDENTS.getIndex());
        
        if (isBlank(studentObj)) {
            throw new StudentDataNullException(rowNum);
        }
        
        String studentData = studentObj.toString().trim();
        String[] tokens = studentData.split("\\s+");
        
        if (tokens.length % 2 != 0) {
            throw new StudentDataFormatException(rowNum, studentData);
        }
        
        for (int i = 0; i < tokens.length; i += 2) {
            try {
                long studentNumberLong = Long.parseLong(tokens[i]);
                int studentNumber = (int) studentNumberLong;
                String studentName = tokens[i + 1];
                
                if (!cache.hasStudent(studentNumber)) {
                    throw new StudentNotExistException(rowNum, studentNumberLong, studentName);
                }
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                throw new StudentDataFormatException(rowNum, studentData);
            }
        }
    }

    private boolean isBlank(Object obj) {
        return obj == null || obj.toString().trim().isEmpty();
    }
}
