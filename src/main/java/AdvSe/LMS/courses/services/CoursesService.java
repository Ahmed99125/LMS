package AdvSe.LMS.courses.services;

import AdvSe.LMS.courses.dtos.CourseDto;
import AdvSe.LMS.courses.dtos.CreateCourseDto;
import AdvSe.LMS.courses.entities.Course;
import AdvSe.LMS.courses.repositories.CoursesRepository;
import AdvSe.LMS.users.entities.Instructor;
import AdvSe.LMS.users.entities.Student;
import AdvSe.LMS.users.repositories.InstructorsRepository;
import AdvSe.LMS.users.repositories.StudentsRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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

    public Course getCourseById(Integer course_id) {
        return courseRepository.findById(course_id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Course not found"));
    }

    public CourseDto getCourseDto(Integer course_id) {
        Course course = getCourseById(course_id);
        return new CourseDto(course);
    }

    public List<CourseDto> getAllCourses() {
        return CourseDto.fromList(courseRepository.findAll());
    }

    public CourseDto createCourse(CreateCourseDto createCourseDto) {
        Instructor instructor = instructorsRepository.findById(createCourseDto.getInstructorId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Instructor not found"));
        Course course = new Course();
        course.setName(createCourseDto.getName());
        course.setInstructor(instructor);
        course.setDescription(createCourseDto.getDescription());
        course.setCourseCode(createCourseDto.getCourseCode());
        return new CourseDto(courseRepository.save(course));
    }

    public CourseDto updateCourse(Integer course_id, CreateCourseDto createCourseDto) {
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
        return new CourseDto(courseRepository.save(course));

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
}
