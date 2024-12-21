package AdvSe.LMS.courses.controllers;

import AdvSe.LMS.courses.dtos.CreateCourseDto;
import AdvSe.LMS.courses.dtos.UpdateCourseDto;
import AdvSe.LMS.courses.entities.Course;
import AdvSe.LMS.courses.services.CoursesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CoursesController {
    private final CoursesService coursesService;

    public CoursesController(CoursesService coursesService) {
        this.coursesService = coursesService;
    }

    @GetMapping("/{courseId}")
    Course getCourseById(@PathVariable("courseId") Integer courseId) {
        return coursesService.getCourseById(courseId);
    }

    @GetMapping("")
    List<Course> getAllCourses() {
        return coursesService.getAllCourses();
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    Course postCourse(
            @Valid @RequestBody CreateCourseDto createCourseDto,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user
    ) {
        createCourseDto.setInstructorId(user.getUsername());
        return coursesService.createCourse(createCourseDto);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @PutMapping("/{courseId}")
    Course updateCourse(
            @PathVariable("courseId") Integer courseId,
            @RequestBody UpdateCourseDto updateCourseDto,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user
    ) {
        return coursesService.updateCourse(courseId, updateCourseDto, user.getUsername());
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    @PutMapping("/{courseId}/students")
    void addStudentToCourse(
            @PathVariable("courseId") Integer courseId,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user
    ) {
        coursesService.addStudentToCourse(courseId, user.getUsername());
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @DeleteMapping("/{courseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCourse(
            @PathVariable("courseId") Integer courseId,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user
    ) {
        coursesService.deleteCourse(courseId, user.getUsername());
    }
}

