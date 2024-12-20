package AdvSe.LMS.courses.services;

import AdvSe.LMS.cloudinary.CloudinaryFile;
import AdvSe.LMS.cloudinary.CloudinaryService;
import AdvSe.LMS.courses.dtos.submissions.AssignmentFeedbackDto;
import AdvSe.LMS.courses.dtos.submissions.AssignmentSubmissionDto;
import AdvSe.LMS.courses.entities.Questions.Assignment;
import AdvSe.LMS.courses.entities.Questions.Submissions.AssignmentSubmission;
import AdvSe.LMS.courses.repositories.AssignmentSubmissionsRepository;
import AdvSe.LMS.courses.repositories.AssignmentsRepository;
import AdvSe.LMS.users.entities.Student;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class AssignmentSubmissionsService {
    private final AssignmentSubmissionsRepository assignmentSubmissionRepository;
    private final AssignmentsRepository assignmentsRepository;
    private final CloudinaryService cloudinaryService;

    AssignmentSubmissionsService(AssignmentSubmissionsRepository assignmentSubmissionRepository, AssignmentsRepository assignmentsRepository, CloudinaryService cloudinaryService) {
        this.assignmentSubmissionRepository = assignmentSubmissionRepository;
        this.assignmentsRepository = assignmentsRepository;
        this.cloudinaryService = cloudinaryService;
    }

    private Assignment getAssignmentOrThrow(Integer assignmentId) {
        return assignmentsRepository.findById(assignmentId).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Assignment not found"));
    }

    public List<AssignmentSubmission> getAssignmentSubmissions(Integer assignmentId, String instructorId) {
        Assignment assignment = getAssignmentOrThrow(assignmentId);

        if (!assignment.getCourse().getInstructor().getId().equals(instructorId))
            throw new ResponseStatusException(NOT_FOUND, "This course does not belong to you");

        return assignment.getSubmissions();
    }


    public AssignmentSubmission getAssignmentSubmissionByStudentId(Integer assignmentId, String studentId, String instructorId) {
        Assignment assignment = getAssignmentOrThrow(assignmentId);

        if (!assignment.getCourse().getInstructor().getId().equals(instructorId))
            throw new ResponseStatusException(NOT_FOUND, "This course does not belong to you");

        return getMySubmission(assignmentId, studentId);
    }


    public AssignmentSubmission getMySubmission(Integer assignmentId, String studentId) {
        return assignmentSubmissionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Assignment submission not found"));
    }

    public AssignmentSubmission submitAssignment(AssignmentSubmissionDto assignmentSubmissionDto) {
        Assignment assignment = getAssignmentOrThrow(assignmentSubmissionDto.getAssignmentId());

        Student student = assignment.getCourse().getStudents().stream().filter(s -> s.getId().equals(assignmentSubmissionDto.getStudentId()))
                .findFirst().orElseThrow(() -> new ResponseStatusException(FORBIDDEN, "You are not enrolled in this course"));

        AssignmentSubmission assignmentSubmission = assignmentSubmissionRepository.findByAssignmentIdAndStudentId(assignment.getId(), student.getId()).orElse(new AssignmentSubmission());

        assignmentSubmission.setAssignment(assignment);
        assignmentSubmission.setStudent(student);

        assignmentSubmission.getSubmissionFiles().clear();
        for (MultipartFile file : assignmentSubmissionDto.getFiles()) {
            CloudinaryFile cloudinaryFile = cloudinaryService.uploadFile(file, "assignment_submissions");
            assignmentSubmission.addSubmissionFile(cloudinaryFile);
        }

        return assignmentSubmissionRepository.save(assignmentSubmission);
    }

    public AssignmentSubmission gradeAssignment(AssignmentFeedbackDto assignmentFeedbackDto) {
        AssignmentSubmission assignmentSubmission = getAssignmentSubmissionByStudentId(
                assignmentFeedbackDto.getAssignmentId(),
                assignmentFeedbackDto.getStudentId(),
                assignmentFeedbackDto.getInstructorId()
        );

        assignmentSubmission.setScore(assignmentFeedbackDto.getScore());
        assignmentSubmission.setFeedback(assignmentFeedbackDto.getFeedback());

        return assignmentSubmissionRepository.save(assignmentSubmission);
    }
}