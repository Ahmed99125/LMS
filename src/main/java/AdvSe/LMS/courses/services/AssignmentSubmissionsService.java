package AdvSe.LMS.courses.services;

import AdvSe.LMS.cloudinary.CloudinaryFile;
import AdvSe.LMS.cloudinary.CloudinaryService;
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

    AssignmentSubmissionsService(AssignmentSubmissionsRepository assignmentSubmissionRepository,
                                 AssignmentsRepository assignmentsRepository,
                                 CloudinaryService cloudinaryService) {
        this.assignmentSubmissionRepository = assignmentSubmissionRepository;
        this.assignmentsRepository = assignmentsRepository;
        this.cloudinaryService = cloudinaryService;
    }

    public List<AssignmentSubmission> getAssignmentSubmissions(Integer assignment_id, String instructorId) {
        Assignment assignment = assignmentsRepository.findById(assignment_id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Assignment not found"));

        if (!assignment.getCourse().getInstructor().getId().equals(instructorId))
            throw new ResponseStatusException(NOT_FOUND, "This course does not belong to you");

        return assignment.getSubmissions();
    }


    public AssignmentSubmission getAssignmentSubmission(Integer assignmentId, String studentId, String instructorId) {
        Assignment assignment = assignmentsRepository.findById(assignmentId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Assignment not found"));

        if (!assignment.getCourse().getInstructor().getId().equals(instructorId))
            throw new ResponseStatusException(NOT_FOUND, "This course does not belong to you");

        return assignment.getSubmissions().stream()
                .filter(submission -> submission.getStudent().getId().equals(studentId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Submission not found"));
    }


    public AssignmentSubmission getMyAssignmentSubmission(Integer assignmentId, String studentId) {
        Assignment assignment = assignmentsRepository.findById(assignmentId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Assignment not found"));

        return assignment.getSubmissions().stream()
                .filter(submission -> submission.getStudent().getId().equals(studentId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Submission not found"));
    }

    public AssignmentSubmission submitAssignment(Integer assignmentId, List<MultipartFile> files, String studentId) {
        Assignment assignment = assignmentsRepository.findById(assignmentId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Assignment not found"));

        Student student = assignment.getCourse().getStudents().stream().filter(s -> s.getId().equals(studentId)).findFirst()
                .orElseThrow(() -> new ResponseStatusException(FORBIDDEN, "You are not enrolled in this course"));

        AssignmentSubmission assignmentSubmission = new AssignmentSubmission();
        assignmentSubmission.setAssignment(assignment);
        assignmentSubmission.setStudent(student);

        for (MultipartFile file : files) {
            CloudinaryFile cloudinaryFile = cloudinaryService.uploadFile(file, "assignment_submissions");
            assignmentSubmission.addSubmissionFile(cloudinaryFile);
        }

        return assignmentSubmissionRepository.save(assignmentSubmission);
    }

    public AssignmentSubmission gradeAssignment(Integer assignmentId, String studentId, AssignmentSubmissionDto assignmentSubmissionDto, String username) {
        Assignment assignment = assignmentsRepository.findById(assignmentId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Assignment not found"));

        if (!assignment.getCourse().getInstructor().getId().equals(username))
            throw new ResponseStatusException(FORBIDDEN, "This course does not belong to you");

        AssignmentSubmission assignmentSubmission = assignment.getSubmissions().stream()
                .filter(submission -> submission.getStudent().getId().equals(studentId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Submission not found"));

        assignmentSubmission.setScore(assignmentSubmissionDto.getScore());
        assignmentSubmission.setFeedback(assignmentSubmissionDto.getFeedback());

        return assignmentSubmissionRepository.save(assignmentSubmission);
    }
}