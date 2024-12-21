package AdvSe.LMS.courses.controllers;

import AdvSe.LMS.courses.dtos.submissions.AssignmentFeedbackDto;
import AdvSe.LMS.courses.dtos.submissions.AssignmentSubmissionDto;
import AdvSe.LMS.courses.entities.Questions.Submissions.AssignmentSubmission;
import AdvSe.LMS.courses.services.AssignmentSubmissionsService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments/{assignmentId}/submissions")
public class AssignmentSubmissionsController {
    private final AssignmentSubmissionsService assignmentSubmissionsService;

    AssignmentSubmissionsController(AssignmentSubmissionsService assignmentSubmissionsService) {
        this.assignmentSubmissionsService = assignmentSubmissionsService;
    }

    // This method is used to get all the submissions for a particular assignment
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @GetMapping("")
    public List<AssignmentSubmission> getAssignmentSubmissions(
            @PathVariable("assignmentId") Integer assignmentId,
            @AuthenticationPrincipal User user
    ) {
        return assignmentSubmissionsService.getAssignmentSubmissions(assignmentId, user.getUsername());
    }

    // This method is used to get the submission of a particular student for a particular assignment
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @GetMapping("/{studentId}")
    public AssignmentSubmission getAssignmentSubmission(
            @PathVariable("assignmentId") Integer assignmentId,
            @PathVariable("studentId") String studentId,
            @AuthenticationPrincipal User user
    ) {
        return assignmentSubmissionsService.getAssignmentSubmissionByStudentId(assignmentId, studentId, user.getUsername());
    }


    // This method is used to get the submission of the current student for a particular assignment
    @PreAuthorize("hasAuthority('STUDENT')")
    @GetMapping("/my_submission")
    public AssignmentSubmission getMyAssignmentSubmission(
            @PathVariable("assignmentId") Integer assignmentId,
            @AuthenticationPrincipal User user
    ) {
        return assignmentSubmissionsService.getMySubmission(assignmentId, user.getUsername());
    }

    // This method is used to submit an assignment
    @PreAuthorize("hasAuthority('STUDENT')")
    @PostMapping(path = "", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public AssignmentSubmission submitAssignment(
            @Valid @ModelAttribute AssignmentSubmissionDto assignmentSubmissionDto,
            @AuthenticationPrincipal User user
    ) {
        return assignmentSubmissionsService.submitAssignment(assignmentSubmissionDto);
    }

    // This method is used to grade an assignment
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @PutMapping("/{studentId}/grade")
    public AssignmentSubmission gradeAssignment(
            @PathVariable("assignmentId") Integer assignmentId,
            @PathVariable("studentId") String studentId,
            @Valid @RequestBody AssignmentFeedbackDto assignmentFeedbackDto,
            @AuthenticationPrincipal User user
    ) {
        assignmentFeedbackDto.setAssignmentId(assignmentId);
        assignmentFeedbackDto.setStudentId(studentId);
        assignmentFeedbackDto.setInstructorId(user.getUsername());
        return assignmentSubmissionsService.gradeAssignment(assignmentFeedbackDto);
    }
}