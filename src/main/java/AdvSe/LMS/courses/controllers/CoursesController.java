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

    @GetMapping("/{course_id}")
    Course getCourseById(@PathVariable("course_id") Integer course_id) {
        return coursesService.getCourseById(course_id);
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
    @PutMapping("/{course_id}")
    Course updateCourse(
            @PathVariable("course_id") Integer course_id,
            @RequestBody UpdateCourseDto updateCourseDto,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user
    ) {
        return coursesService.updateCourse(course_id, updateCourseDto, user.getUsername());
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    @PutMapping("/{course_id}/students")
    void addStudentToCourse(
            @PathVariable("course_id") Integer course_id,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user
    ) {
        coursesService.addStudentToCourse(course_id, user.getUsername());
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @DeleteMapping("/{course_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCourse(
            @PathVariable("course_id") Integer course_id,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user
    ) {
        coursesService.deleteCourse(course_id, user.getUsername());
    }
}

