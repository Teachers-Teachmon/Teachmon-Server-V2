package solvit.teachmon.domain.after_school.application.service;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolEntity;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolStudentEntity;
import solvit.teachmon.domain.after_school.domain.enums.AfterSchoolSpreadSheetsColumn;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolRepository;
import solvit.teachmon.domain.after_school.domain.service.AfterSchoolStudentDomainService;
import solvit.teachmon.domain.after_school.exception.EmptySpreadSheetException;
import solvit.teachmon.domain.after_school.exception.EmptySpreadSheetHeaderCellException;
import solvit.teachmon.domain.after_school.exception.PeriodInvalidException;
import solvit.teachmon.domain.after_school.exception.PlaceNotExistException;
import solvit.teachmon.domain.after_school.exception.SpreadSheetHeaderMismatchException;
import solvit.teachmon.domain.after_school.exception.SpreadSheetHeaderSizeMismatchException;
import solvit.teachmon.domain.after_school.exception.StudentDataFormatException;
import solvit.teachmon.domain.after_school.exception.TeacherNotExistException;
import solvit.teachmon.domain.after_school.exception.WeekDayInvalidException;
import solvit.teachmon.domain.branch.domain.entity.BranchEntity;
import solvit.teachmon.domain.branch.exception.BranchNotFoundException;
import solvit.teachmon.domain.branch.domain.repository.BranchRepository;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.management.student.domain.repository.StudentRepository;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;
import solvit.teachmon.domain.place.domain.repository.PlaceRepository;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;
import solvit.teachmon.global.enums.SchoolPeriod;
import solvit.teachmon.global.enums.WeekDay;
import solvit.teachmon.global.properties.GoogleSpreadSheetProperties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@ConditionalOnBean(Sheets.class)
@RequiredArgsConstructor
public class AfterSchoolSpreadSheetService {
    private final Sheets sheets;
    private final GoogleSpreadSheetProperties googleSpreadSheetProperties;
    private final AfterSchoolRowValidator validator;
    private final AfterSchoolRepository afterSchoolRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final BranchRepository branchRepository;
    private final PlaceRepository placeRepository;
    private final AfterSchoolStudentDomainService afterSchoolStudentDomainService;

    @Transactional
    public void flushToSpreadSheet(String spreadSheetId) throws IOException {
        List<AfterSchoolEntity> activeAfterSchools = afterSchoolRepository.findActiveAfterSchoolsWithRelations();
        
        List<List<Object>> values = new ArrayList<>();
        
        values.add(Arrays.asList(
                AfterSchoolSpreadSheetsColumn.YEAR.getHeaderName(),
                AfterSchoolSpreadSheetsColumn.BRANCH.getHeaderName(),
                AfterSchoolSpreadSheetsColumn.WEEKDAY.getHeaderName(),
                AfterSchoolSpreadSheetsColumn.GRADE.getHeaderName(),
                AfterSchoolSpreadSheetsColumn.PERIOD.getHeaderName(),
                AfterSchoolSpreadSheetsColumn.TEACHER.getHeaderName(),
                AfterSchoolSpreadSheetsColumn.PLACE.getHeaderName(),
                AfterSchoolSpreadSheetsColumn.NAME.getHeaderName(),
                AfterSchoolSpreadSheetsColumn.STUDENTS.getHeaderName()
        ));
        
        for (AfterSchoolEntity afterSchool : activeAfterSchools) {
            String weekDayKorean = WeekDay.convertWeekDayToKorean(afterSchool.getWeekDay());
            String periodKorean = SchoolPeriod.convertPeriodToKorean(afterSchool.getPeriod());
            String teacherData = afterSchool.getTeacher().getName() + "(" + afterSchool.getTeacher().getMail() + ")";
            String studentsData = afterSchool.getAfterSchoolStudents().stream()
                    .map(ass -> ass.getStudent().getNumber() + " " + ass.getStudent().getName())
                    .collect(Collectors.joining(" "));
            
            values.add(Arrays.asList(
                    afterSchool.getYear(),
                    afterSchool.getBranch().getBranch(),
                    weekDayKorean,
                    afterSchool.getGrade(),
                    periodKorean,
                    teacherData,
                    afterSchool.getPlace().getName(),
                    afterSchool.getName(),
                    studentsData
            ));
        }
        
        ValueRange data = new ValueRange().setValues(values);
        
        sheets.spreadsheets().values()
                .update(spreadSheetId, googleSpreadSheetProperties.getPage(), data)
                .setValueInputOption("RAW")
                .execute();
    }

    // TODO: 이거 Schedule 관련해서 약간 더 추가 필요해보임 새로운 것들은 Schedule 추가하고, 수정한 것들은 Schedule 수정
    @Transactional
    public void uploadSpreadSheet(String spreadSheetId) throws IOException {
        List<List<Object>> data = loadSheetRows(spreadSheetId);
        
        List<AfterSchoolEntity> allAfterSchools = afterSchoolRepository.findAllWithRelations();
        allAfterSchools.forEach(AfterSchoolEntity::endAfterSchool);
        
        ReferenceDataCache cache = preloadReferenceData();
        
        long rowNumber = 0;
        
        for (List<Object> row : data) {
            validator.validate(row, rowNumber, cache);
            
            List<StudentInfo> studentInfos = parseStudentInfos(row, rowNumber);
            
            String compositeKey = generateCompositeKey(row);
            
            Optional<AfterSchoolEntity> existingAfterSchool = findByCompositeKey(compositeKey, allAfterSchools);
            
            if (existingAfterSchool.isPresent()) {
                handleExistingAfterSchool(existingAfterSchool.get(), studentInfos, cache);
            } else {
                createNewAfterSchool(row, studentInfos, cache, rowNumber);
            }
            
            rowNumber++;
        }
    }

    private List<List<Object>> loadSheetRows(String spreadsheetId) throws IOException {
        ValueRange data = sheets.spreadsheets()
                .values()
                .get(spreadsheetId, googleSpreadSheetProperties.getPage())
                .execute();

        List<List<Object>> values = data.getValues();

        if (values == null || values.isEmpty()) {
            throw new EmptySpreadSheetException();
        }

        validateHeader(values.getFirst());

        return values.subList(1, values.size());
    }

    private void validateHeader(List<Object> headerRow) {
        AfterSchoolSpreadSheetsColumn[] columns =
                AfterSchoolSpreadSheetsColumn.values();

        if (headerRow.size() < columns.length) {
            throw new SpreadSheetHeaderSizeMismatchException();
        }

        for (AfterSchoolSpreadSheetsColumn column : columns) {
            Object cell = headerRow.get(column.getIndex());

            if (cell == null) {
                throw new EmptySpreadSheetHeaderCellException(column.getIndex(), 0);
            }

            String actualHeader = cell.toString().trim();

            if (!actualHeader.equals(column.getHeaderName())) {
               throw new SpreadSheetHeaderMismatchException(column.getIndex(), actualHeader, column.getHeaderName());
            }
        }
    }
    
    private ReferenceDataCache preloadReferenceData() {
        List<TeacherEntity> allTeachers = teacherRepository.findAll();
        Map<String, TeacherEntity> teacherEntityMap = allTeachers.stream()
            .collect(Collectors.toMap(TeacherEntity::getMail, Function.identity()));
            
        List<PlaceEntity> allPlaces = placeRepository.findAll();
        Map<String, PlaceEntity> placeEntityMap = allPlaces.stream()
            .collect(Collectors.toMap(PlaceEntity::getName, Function.identity()));
            
        Map<Integer, StudentEntity> studentEntityMap = studentRepository.findAll().stream()
            .collect(Collectors.toMap(StudentEntity::getNumber, Function.identity()));
            
        return new ReferenceDataCache(teacherEntityMap, placeEntityMap, studentEntityMap);
    }
    
    private record StudentInfo(Long number, String name) {}
    
    private List<StudentInfo> parseStudentInfos(List<Object> row, long rowNum) {
        Object studentObj = row.get(AfterSchoolSpreadSheetsColumn.STUDENTS.getIndex());
        String studentData = studentObj.toString().trim();
        String[] tokens = studentData.split("\\s+");
        
        List<StudentInfo> studentInfos = new ArrayList<>();
        
        try {
            for (int i = 0; i < tokens.length; i += 2) {
                long studentNumber = Long.parseLong(tokens[i]);
                String studentName = tokens[i + 1];
                studentInfos.add(new StudentInfo(studentNumber, studentName));
            }
        }
        catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw new StudentDataFormatException(rowNum, studentData);
        }
        
        return studentInfos;
    }
    
    private String generateCompositeKey(List<Object> row) {
        StringBuilder keyBuilder = new StringBuilder();
        
        for (int i = 0; i < 8; i++) {
            if (i > 0) {
                keyBuilder.append("|");
            }
            keyBuilder.append(row.get(i).toString().trim());
        }
        
        return keyBuilder.toString();
    }
    
    private Optional<AfterSchoolEntity> findByCompositeKey(String compositeKey, List<AfterSchoolEntity> allAfterSchools) {
        String[] parts = compositeKey.split("\\|");
        
        Integer year = Integer.valueOf(parts[0]);
        Integer branchNumber = Integer.valueOf(parts[1]);
        WeekDay weekDay = WeekDay.fromKorean(parts[2]);
        Integer grade = Integer.valueOf(parts[3]);
        SchoolPeriod period = convertToSchoolPeriod(parts[4], -1);
        String teacherData = parts[5];
        String placeName = parts[6];
        String name = parts[7];
        
        TeacherDataParser.TeacherInfo teacherInfo = TeacherDataParser.parse(teacherData, -1);
        return allAfterSchools.stream()
            .filter(as -> 
                Objects.equals(year, as.getYear()) &&
                Objects.equals(branchNumber, as.getBranch().getBranch()) &&
                weekDay == as.getWeekDay() &&
                Objects.equals(grade, as.getGrade()) &&
                period == as.getPeriod() &&
                teacherInfo.email().equals(as.getTeacher().getMail()) &&
                placeName.equals(as.getPlace().getName()) &&
                name.equals(as.getName())
            )
            .findFirst();
    }

    private void handleExistingAfterSchool(AfterSchoolEntity existingAfterSchool, List<StudentInfo> studentInfos, ReferenceDataCache cache) {
        existingAfterSchool.resumeAfterSchool();
        
        Set<Integer> currentStudentNumbers = existingAfterSchool.getAfterSchoolStudents().stream()
            .map(AfterSchoolStudentEntity::getStudent)
            .map(StudentEntity::getNumber)
            .collect(Collectors.toSet());
            
        Set<Integer> newStudentNumbers = studentInfos.stream()
            .map(info -> info.number().intValue())
            .collect(Collectors.toSet());
        
        if (!currentStudentNumbers.equals(newStudentNumbers)) {
            afterSchoolStudentDomainService.deleteAllByAfterSchool(existingAfterSchool);
            
            List<StudentEntity> students = studentInfos.stream()
                .map(info -> cache.getStudent(info.number().intValue()))
                .filter(Objects::nonNull)
                .toList();
            afterSchoolStudentDomainService.assignStudents(existingAfterSchool, students);
        }
    }
    
    private void createNewAfterSchool(List<Object> row, List<StudentInfo> studentInfos, ReferenceDataCache cache, long rowNumber) {
        Integer year = Integer.valueOf(row.get(AfterSchoolSpreadSheetsColumn.YEAR.getIndex()).toString().trim());
        Integer branchNumber = Integer.valueOf(row.get(AfterSchoolSpreadSheetsColumn.BRANCH.getIndex()).toString().trim());
        String weekDayStr = row.get(AfterSchoolSpreadSheetsColumn.WEEKDAY.getIndex()).toString().trim();
        Integer grade = Integer.valueOf(row.get(AfterSchoolSpreadSheetsColumn.GRADE.getIndex()).toString().trim());
        String periodStr = row.get(AfterSchoolSpreadSheetsColumn.PERIOD.getIndex()).toString().trim();
        String teacherData = row.get(AfterSchoolSpreadSheetsColumn.TEACHER.getIndex()).toString().trim();
        String placeName = row.get(AfterSchoolSpreadSheetsColumn.PLACE.getIndex()).toString().trim();
        String name = row.get(AfterSchoolSpreadSheetsColumn.NAME.getIndex()).toString().trim();
        
        TeacherDataParser.TeacherInfo teacherInfo = TeacherDataParser.parse(teacherData, rowNumber);
        
        BranchEntity branch = branchRepository.findByYearAndBranch(year, branchNumber)
            .orElseThrow(BranchNotFoundException::new);
        TeacherEntity teacher = cache.getTeacher(teacherInfo.email());
        if (teacher == null) {
            throw new TeacherNotExistException(rowNumber, teacherInfo.email());
        }
        PlaceEntity place = cache.getPlace(placeName);
        if (place == null) {
            throw new PlaceNotExistException(-1, placeName);
        }
        
        WeekDay weekDay = convertToWeekDay(weekDayStr, rowNumber);
        SchoolPeriod period = convertToSchoolPeriod(periodStr, rowNumber);
        
        AfterSchoolEntity afterSchool = AfterSchoolEntity.builder()
            .teacher(teacher)
            .branch(branch)
            .place(place)
            .weekDay(weekDay)
            .period(period)
            .name(name)
            .grade(grade)
            .year(year)
            .build();
            
        afterSchoolRepository.save(afterSchool);
        
        List<StudentEntity> students = studentInfos.stream()
            .map(info -> cache.getStudent(info.number().intValue()))
            .filter(Objects::nonNull)
            .toList();
        afterSchoolStudentDomainService.assignStudents(afterSchool, students);
    }
    
    private WeekDay convertToWeekDay(String weekDayStr, long rowNumber) {
        return switch (weekDayStr) {
            case "월요일" -> WeekDay.MON;
            case "화요일" -> WeekDay.TUE;
            case "수요일" -> WeekDay.WED;
            case "목요일" -> WeekDay.THU;
            default -> throw new WeekDayInvalidException(rowNumber, weekDayStr);
        };
    }
    
    private SchoolPeriod convertToSchoolPeriod(String periodStr, long rowNumber) {
        return switch (periodStr) {
            case "7교시" -> SchoolPeriod.SEVEN_PERIOD;
            case "8~9교시" -> SchoolPeriod.EIGHT_AND_NINE_PERIOD;
            case "10~11교시" -> SchoolPeriod.TEN_AND_ELEVEN_PERIOD;
            default -> throw new PeriodInvalidException(rowNumber, periodStr);
        };
    }
    
    
}
