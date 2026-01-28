package solvit.teachmon.domain.user.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import solvit.teachmon.domain.user.application.service.TeacherService;
import solvit.teachmon.domain.user.presentation.dto.response.TeacherProfileResponseDto;
import solvit.teachmon.global.security.user.TeachmonUserDetails;

@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherController {
    private final TeacherService teacherService;

    @GetMapping("/me")
    public TeacherProfileResponseDto getMyUserProfile(@AuthenticationPrincipal TeachmonUserDetails teachmonUserDetails) {
        return teacherService.getMyUserProfile(teachmonUserDetails);
    }
}
