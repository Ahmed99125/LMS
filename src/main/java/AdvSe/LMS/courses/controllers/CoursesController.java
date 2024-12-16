package AdvSe.LMS.courses.controllers;

import AdvSe.LMS.courses.dtos.CourseDto;
import AdvSe.LMS.courses.dtos.CreateCourseDto;
import AdvSe.LMS.courses.entities.Course;
import AdvSe.LMS.courses.repositories.CoursesRepository;
import AdvSe.LMS.courses.services.CoursesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CoursesController {
    private final CoursesService coursesService;
    private final CoursesRepository coursesRepository;

    public CoursesController(CoursesService coursesService, CoursesRepository coursesRepository) {
        this.coursesService = coursesService;
        this.coursesRepository = coursesRepository;
    }

    @GetMapping("/{course_id}")
    CourseDto getCourseById(@PathVariable("course_id") Integer course_id) {
        return coursesService.getCourseDto(course_id);
    }

    @GetMapping("")
    List<CourseDto> getAllCourses() {
        return coursesService.getAllCourses();
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    CourseDto postCourse(
            @Valid @RequestBody CreateCourseDto createCourseDto
    ) {
        return coursesService.createCourse(createCourseDto);
    }

    @PutMapping("/{course_id}")
    CourseDto updateCourse(
            @PathVariable("course_id") Integer course_id,
            @Valid @RequestBody CreateCourseDto createCourseDto
    ) {
        return coursesService.updateCourse(course_id, createCourseDto);
    }

    @PutMapping("/{course_id}/students")
    void addStudentToCourse(
            @PathVariable("course_id") Integer course_id,
            @RequestBody() String student_id
    ) {
        coursesService.addStudentToCourse(course_id, student_id);
    }

    @DeleteMapping("/{course_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCourse(@PathVariable("course_id") Integer course_id) {
        Course course = coursesService.getCourseById(course_id);
        coursesRepository.delete(course);
    }
}

