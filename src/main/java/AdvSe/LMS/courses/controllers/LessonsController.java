package AdvSe.LMS.courses.controllers;

import AdvSe.LMS.courses.dtos.CreateLessonDto;
import AdvSe.LMS.courses.dtos.UpdateLessonDto;
import AdvSe.LMS.courses.entities.Lessons.Lesson;
import AdvSe.LMS.courses.services.LessonsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
public class LessonsController {
    private final LessonsService lessonsService;

    public LessonsController(LessonsService lessonsService) {
        this.lessonsService = lessonsService;
    }

    @GetMapping("")
    List<Lesson> getLessons(@RequestParam Integer courseId) {
        return lessonsService.getLessonsByCourseId(courseId);
    }

    @GetMapping("/{lessonId}")
    Lesson getLessonById(@PathVariable("lessonId") Integer lessonId) {
        return lessonsService.getLessonById(lessonId);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @PostMapping(path = "", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    Lesson postLesson(
            @ModelAttribute CreateLessonDto createLessonDto,
            @AuthenticationPrincipal User user
    ) {
        createLessonDto.setInstructorId(user.getUsername());
        return lessonsService.createLesson(createLessonDto);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @PutMapping(path = "/{lessonId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    Lesson updateLesson(
            @ModelAttribute UpdateLessonDto updateLessonDto,
            @AuthenticationPrincipal User user
    ) {
        updateLessonDto.setInstructorId(user.getUsername());
        return lessonsService.updateLesson(updateLessonDto);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @DeleteMapping("/{lessonId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteLesson(
            @PathVariable("lessonId") Integer lessonId,
            @AuthenticationPrincipal User user
    ) {
        lessonsService.deleteLesson(lessonId, user.getUsername());
    }
}
