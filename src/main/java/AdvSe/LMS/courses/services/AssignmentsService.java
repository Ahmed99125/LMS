package AdvSe.LMS.courses.services;

import AdvSe.LMS.cloudinary.CloudinaryFile;
import AdvSe.LMS.cloudinary.CloudinaryService;
import AdvSe.LMS.courses.dtos.CreateAssignmentDto;
import AdvSe.LMS.courses.dtos.UpdateAssignmentDto;
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

    public Assignment getAssignmentById(Integer assignmentId) {
        return assignmentsRepository.findById(assignmentId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Assignment not found"));
    }

    public List<Assignment> getAssignmentsByCourseId(Integer courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Course not found"));

        return course.getAssignments();
    }

    public Assignment createAssignment(CreateAssignmentDto createAssignmentDto) {
        Course course = courseRepository.findById(createAssignmentDto.getCourseId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Course not found"));

        if (!course.getInstructor().getId().equals(createAssignmentDto.getInstructorId()))
            throw new ResponseStatusException(FORBIDDEN, "This course does not belong to you");

        Assignment assignment = new Assignment();
        assignment.setCourse(course);
        assignment.setName(createAssignmentDto.getName());
        for (MultipartFile file : createAssignmentDto.getFiles()) {
            CloudinaryFile cloudinaryFile = cloudinaryService.uploadFile(file, "assignments");
            assignment.addAssignmentFile(cloudinaryFile);
        }

        return assignmentsRepository.save(assignment);
    }

    public Assignment updateAssignment(UpdateAssignmentDto updateAssignmentDto) {
        Assignment assignment = getAssignmentById(updateAssignmentDto.getAssignmentId());

        if (!assignment.getCourse().getInstructor().getId().equals(updateAssignmentDto.getInstructorId()))
            throw new ResponseStatusException(FORBIDDEN, "This course does not belong to you");

        if (updateAssignmentDto.getName() != null)
            assignment.setName(updateAssignmentDto.getName());

        if (updateAssignmentDto.getFiles() != null) {
            assignment.getAssignmentFiles().clear();
            for (MultipartFile file : updateAssignmentDto.getFiles()) {
                CloudinaryFile cloudinaryFile = cloudinaryService.uploadFile(file, "assignments");
                assignment.addAssignmentFile(cloudinaryFile);
            }
        }

        return assignmentsRepository.save(assignment);
    }

    public void deleteAssignment(Integer assignmentId, String instructorId) {
        Assignment assignment = getAssignmentById(assignmentId);

        if (!assignment.getCourse().getInstructor().getId().equals(instructorId))
            throw new ResponseStatusException(FORBIDDEN, "This course does not belong to you");

        assignmentsRepository.delete(assignment);
    }
}
