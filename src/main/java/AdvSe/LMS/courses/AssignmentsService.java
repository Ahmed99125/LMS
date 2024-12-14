package AdvSe.LMS.courses;

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

    AssignmentsService(AssignmentsRepository assignmentsRepository,
                       CoursesRepository courseRepository, CloudinaryService cloudinaryService) {
        this.assignmentsRepository = assignmentsRepository;
        this.courseRepository = courseRepository;
        this.cloudinaryService = cloudinaryService;
    }

    public List<Assignment> getAssignmentsByCourseId(Integer course_id) {
        Optional<Course> course = courseRepository.findById(course_id);

        if (course.isEmpty())
            return null;

        return course.get().getAssignments();
    }

    public Assignment createAssignment(Integer course_id, String name, List<MultipartFile> files) {
        Optional<Course> course = courseRepository.findById(course_id);
        if (course.isEmpty())
            throw new ResponseStatusException(NOT_FOUND, "Course not found");
        Assignment assignment = new Assignment();
        assignment.setCourse(course.get());
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

    public Assignment updateAssignment(Integer assignment_id, String name, List<MultipartFile> files) {
        Optional<Assignment> opt_assignment = assignmentsRepository.findById(assignment_id);
        if (opt_assignment.isEmpty())
            throw new ResponseStatusException(NOT_FOUND, "Assignment not found");
        Assignment assignment = opt_assignment.get();
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
