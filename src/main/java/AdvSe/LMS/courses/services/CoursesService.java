package AdvSe.LMS.courses.services;

import AdvSe.LMS.courses.dtos.CreateCourseDto;
import AdvSe.LMS.courses.dtos.UpdateCourseDto;
import AdvSe.LMS.courses.entities.Course;
import AdvSe.LMS.courses.repositories.CoursesRepository;
import AdvSe.LMS.users.entities.Instructor;
import AdvSe.LMS.users.entities.Student;
import AdvSe.LMS.users.repositories.InstructorsRepository;
import AdvSe.LMS.users.repositories.StudentsRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class CoursesService {
    private final CoursesRepository courseRepository;
    private final InstructorsRepository instructorsRepository;
    private final StudentsRepository studentsRepository;

    public CoursesService(CoursesRepository courseRepository, InstructorsRepository instructorsRepository, StudentsRepository studentsRepository) {
        this.courseRepository = courseRepository;
        this.instructorsRepository = instructorsRepository;
        this.studentsRepository = studentsRepository;
    }

    public Course getCourseById(Integer courseId) {
        return courseRepository.findById(courseId)
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

    public Course updateCourse(Integer courseId, UpdateCourseDto updateCourseDto, String instructorId) {
        Course course = getCourseById(courseId);
        if (!course.getInstructor().getId().equals(instructorId))
            throw new ResponseStatusException(FORBIDDEN, "This course does not belong to you");

        if (updateCourseDto.getName() != null)
            course.setName(updateCourseDto.getName());
        if (updateCourseDto.getDescription() != null)
            course.setDescription(updateCourseDto.getDescription());
        if (updateCourseDto.getCourseCode() != null)
            course.setCourseCode(updateCourseDto.getCourseCode());
        return courseRepository.save(course);

    }

    public void addStudentToCourse(Integer courseId, String studentId) {
        Course course = getCourseById(courseId);
        Student student = studentsRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Student not found"));
        course.addStudent(student);
        student.addCourse(course);
        courseRepository.save(course);
        studentsRepository.save(student);
    }

    public void deleteCourse(Integer courseId, String instructorId) {
        Course course = getCourseById(courseId);
        if (!course.getInstructor().getId().equals(instructorId))
            throw new ResponseStatusException(FORBIDDEN, "This course does not belong to you");
        courseRepository.delete(course);
    }
}
