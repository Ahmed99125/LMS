package AdvSe.LMS.courses.services;

import AdvSe.LMS.courses.dtos.CreateCourseDto;
import AdvSe.LMS.courses.entities.Course;
import AdvSe.LMS.courses.repositories.CoursesRepository;
import AdvSe.LMS.users.entities.Instructor;
import AdvSe.LMS.users.repositories.InstructorsRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class CoursesService {
    private final CoursesRepository courseRepository;
    private final InstructorsRepository instructorsRepository;

    public CoursesService(CoursesRepository courseRepository, InstructorsRepository instructorsRepository) {
        this.courseRepository = courseRepository;
        this.instructorsRepository = instructorsRepository;
    }

    public Course getCourseById(Integer course_id) {
        return courseRepository.findById(course_id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Course not found"));
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course createCourse(CreateCourseDto createCourseDto) {
        Instructor instructor = instructorsRepository.findById(createCourseDto.getInstructorId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Instructor not found"));
        Course course = new Course();
        course.setName(createCourseDto.getName());
        course.setInstructor(instructor);
        course.setDescription(createCourseDto.getDescription());
        course.setCourseCode(createCourseDto.getCourseCode());
        return courseRepository.save(course);
    }

    public Course updateCourse(Integer course_id, CreateCourseDto createCourseDto) {
        Course course = getCourseById(course_id);

        if (createCourseDto.getName() != null)
            course.setName(createCourseDto.getName());
        if (createCourseDto.getDescription() != null)
            course.setDescription(createCourseDto.getDescription());
        if (createCourseDto.getCourseCode() != null)
            course.setCourseCode(createCourseDto.getCourseCode());
        if (createCourseDto.getInstructorId() != null) {
            Instructor instructor = instructorsRepository.findById(createCourseDto.getInstructorId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Instructor not found"));
            course.setInstructor(instructor);
        }
        return courseRepository.save(course);

    }
}
