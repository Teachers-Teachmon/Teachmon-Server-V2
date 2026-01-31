package solvit.teachmon.domain.after_school.presentation.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import solvit.teachmon.domain.after_school.application.service.AfterSchoolSpreadSheetService;

import java.io.IOException;

@RestController
@RequestMapping("/afterschool")
@ConditionalOnBean(AfterSchoolSpreadSheetService.class)
@RequiredArgsConstructor
@Validated
public class AfterSchoolSpreadSheetController {
    private final AfterSchoolSpreadSheetService afterSchoolService;

    @PostMapping("/upload/{spreadSheetId}")
    public ResponseEntity<Void> uploadAfterSchool(@PathVariable @NotNull(message = "스프레드 시트 아이디는 필수입니다.") String spreadSheetId) throws IOException {
        afterSchoolService.uploadSpreadSheet(spreadSheetId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/flush/{spreadSheetId}")
    public ResponseEntity<Void> syncAfterSchool(@PathVariable @NotNull(message = "스프레드 시트 아이디는 필수입니다.") String spreadSheetId) throws IOException {
        afterSchoolService.flushToSpreadSheet(spreadSheetId);
        return ResponseEntity.noContent().build();
    }
}
