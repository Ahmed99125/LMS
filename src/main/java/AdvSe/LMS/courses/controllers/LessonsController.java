package AdvSe.LMS.courses.controllers;

import AdvSe.LMS.courses.entities.Lessons.Lesson;
import AdvSe.LMS.courses.repositories.LessonsRepository;
import AdvSe.LMS.courses.services.LessonsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/courses/{course_id}/lessons")
public class LessonsController {
    private final LessonsService lessonsService;
    private final LessonsRepository lessonsRepository;

    public LessonsController(LessonsService lessonsService, LessonsRepository lessonsRepository) {
        this.lessonsService = lessonsService;
        this.lessonsRepository = lessonsRepository;
    }

    @GetMapping("")
    List<Lesson> getLessons(@PathVariable("course_id") Integer course_id) {
        return lessonsService.getLessonsByCourseId(course_id);
    }

    @GetMapping("/{lesson_id}")
    Lesson getLessonById(
            @PathVariable("course_id") Integer course_id,
            @PathVariable("lesson_id") Integer lesson_id) {
        return lessonsService.getLessonById(course_id, lesson_id);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    Lesson postLesson(
            @PathVariable("course_id") Integer course_id,
            @RequestPart("data") String name,
            @RequestPart("files") List<MultipartFile> files) {
        log.info("Creating lesson for course {}", course_id);
        log.info("Name: {}", name);
        log.info("Files: {}", files);
        return lessonsService.createLesson(course_id, name, files);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @PutMapping("/{lesson_id}")
    Lesson updateLesson(
            @PathVariable("course_id") Integer course_id,
            @PathVariable("lesson_id") Integer lesson_id,
            @RequestPart("data") String name,
            @RequestPart("files") List<MultipartFile> files) {
        return lessonsService.updateLesson(course_id, lesson_id, name, files);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @DeleteMapping("/{lesson_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteLesson(
            @PathVariable("course_id") Integer course_id,
            @PathVariable("lesson_id") Integer lesson_id) {
        Lesson lesson = lessonsService.getLessonById(course_id, lesson_id);
        lessonsRepository.delete(lesson);
    }
}
