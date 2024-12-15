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

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
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

    public Assignment getAssignmentById(Integer course_id, Integer assignment_id) {
        Course course = courseRepository.findById(course_id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Course not found"));

        Assignment assignment = assignmentsRepository.findById(assignment_id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Assignment not found"));

        if (assignment.getCourse().getId().equals(course_id))
            throw new ResponseStatusException(NOT_FOUND, "Assignment not found");

        return assignment;
    }

    public List<Assignment> getAssignmentsByCourseId(Integer course_id) {
        Course course = courseRepository.findById(course_id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Course not found"));

        return course.getAssignments();
    }

    public Assignment createAssignment(Integer course_id, String name, List<MultipartFile> files) {
        Course course = courseRepository.findById(course_id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Course not found"));

        Assignment assignment = new Assignment();
        assignment.setCourse(course);
        assignment.setName(name);
        try {
            List<CloudinaryFile> cloudinaryFiles = cloudinaryService.uploadMultipleFiles(files, "assignments");
            assignment.setAssignmentFiles(cloudinaryFiles);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResponseStatusException(BAD_REQUEST, "Can't read files");
        }

        return assignmentsRepository.save(assignment);
    }

    public Assignment updateAssignment(Integer course_id, Integer assignment_id, String name, List<MultipartFile> files) {
        Assignment assignment = getAssignmentById(course_id, assignment_id);

        assignment.setName(name);
        try {
            List<CloudinaryFile> cloudinaryFiles = cloudinaryService.uploadMultipleFiles(files, "assignments");
            assignment.setAssignmentFiles(cloudinaryFiles);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResponseStatusException(BAD_REQUEST, "Can't read files");
        }
        return assignmentsRepository.save(assignment);
    }
}
