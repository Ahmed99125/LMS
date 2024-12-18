package AdvSe.LMS.courses.services;

import AdvSe.LMS.cloudinary.CloudinaryFile;
import AdvSe.LMS.cloudinary.CloudinaryService;
import AdvSe.LMS.courses.entities.Course;
import AdvSe.LMS.courses.entities.Questions.Assignment;
import AdvSe.LMS.courses.repositories.AssignmentsRepository;
import AdvSe.LMS.courses.repositories.CoursesRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class AssignmentsService {
    private final AssignmentsRepository assignmentsRepository;
    private final CoursesRepository courseRepository;
    private final CloudinaryService cloudinaryService;

    AssignmentsService(AssignmentsRepository assignmentsRepository, CoursesRepository courseRepository, CloudinaryService cloudinaryService) {
        this.assignmentsRepository = assignmentsRepository;
        this.courseRepository = courseRepository;
        this.cloudinaryService = cloudinaryService;
    }

    private Assignment getCheckedAssignmentById(Integer course_id, Integer assignment_id, String instructorId) {
        Assignment assignment = getAssignmentById(course_id, assignment_id);
        if (!assignment.getCourse().getInstructor().getId().equals(instructorId))
            throw new ResponseStatusException(FORBIDDEN, "This course does not belong to you");
        return assignment;
    }

    public Assignment getAssignmentById(Integer course_id, Integer assignment_id) {
        if (!courseRepository.existsById(course_id)) {
            throw new ResponseStatusException(NOT_FOUND, "Course not found");
        }

        Assignment assignment = assignmentsRepository.findById(assignment_id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Assignment not found"));

        if (!assignment.getCourse().getId().equals(course_id))
            throw new ResponseStatusException(NOT_FOUND, "Assignment not found");

        return assignment;
    }

    public List<Assignment> getAssignmentsByCourseId(Integer course_id) {
        Course course = courseRepository.findById(course_id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Course not found"));

        return course.getAssignments();
    }

    public Assignment createAssignment(Integer course_id, String instructorId, String name, List<MultipartFile> files) {
        Course course = courseRepository.findById(course_id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Course not found"));
        if (!course.getInstructor().getId().equals(instructorId))
            throw new ResponseStatusException(FORBIDDEN, "This course does not belong to you");

        Assignment assignment = new Assignment();
        assignment.setCourse(course);
        assignment.setName(name);
        for (MultipartFile file : files) {
            CloudinaryFile cloudinaryFile = cloudinaryService.uploadFile(file, "assignments");
            assignment.addAssignmentFile(cloudinaryFile);
        }

        return assignmentsRepository.save(assignment);
    }

    public Assignment updateAssignment(Integer course_id, String instructorId, Integer assignment_id, String name, List<MultipartFile> files) {
        Assignment assignment = getCheckedAssignmentById(course_id, assignment_id, instructorId);

        assignment.setName(name);
        for (MultipartFile file : files) {
            CloudinaryFile cloudinaryFile = cloudinaryService.uploadFile(file, "assignments");
            assignment.addAssignmentFile(cloudinaryFile);
        }

        return assignmentsRepository.save(assignment);
    }

    public void deleteAssignment(Integer course_id, String instructorId, Integer assignment_id) {
        Assignment assignment = getCheckedAssignmentById(course_id, assignment_id, instructorId);
        assignmentsRepository.delete(assignment);
    }
}
