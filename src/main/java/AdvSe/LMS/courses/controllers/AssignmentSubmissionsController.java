package AdvSe.LMS.courses.controllers;

import AdvSe.LMS.courses.dtos.submissions.AssignmentSubmissionDto;
import AdvSe.LMS.courses.entities.Questions.Submissions.AssignmentSubmission;
import AdvSe.LMS.courses.services.AssignmentSubmissionsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/assignments/{assignment_id}/submissions")
public class AssignmentSubmissionsController {
    private final AssignmentSubmissionsService assignmentSubmisstionService;

    AssignmentSubmissionsController(AssignmentSubmissionsService assignmentSubmisstionService) {
        this.assignmentSubmisstionService = assignmentSubmisstionService;
    }

    // This method is used to get all the submissions for a particular assignment
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @GetMapping("")
    public List<AssignmentSubmission> getAssignmentSubmissions(
            @PathVariable("assignment_id") Integer assignment_id,
            @AuthenticationPrincipal User user
    ) {
        return assignmentSubmisstionService.getAssignmentSubmissions(assignment_id, user.getUsername());
    }

    // This method is used to get the submission of a particular student for a particular assignment
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @GetMapping("/{student_id}")
    public AssignmentSubmission getAssignmentSubmission(
            @PathVariable("assignment_id") Integer assignment_id,
            @PathVariable("student_id") String student_id,
            @AuthenticationPrincipal User user
    ) {
        return assignmentSubmisstionService.getAssignmentSubmission(assignment_id, student_id, user.getUsername());
    }


    // This method is used to get the submission of the current student for a particular assignment
    @PreAuthorize("hasAuthority('STUDENT')")
    @GetMapping("/my_submission")
    public AssignmentSubmission getMyAssignmentSubmission(
            @PathVariable("assignment_id") Integer assignment_id,
            @AuthenticationPrincipal User user
    ) {
        return assignmentSubmisstionService.getMyAssignmentSubmission(assignment_id, user.getUsername());
    }

    // This method is used to submit an assignment
    @PreAuthorize("hasAuthority('STUDENT')")
    @PostMapping("")
    public AssignmentSubmission submitAssignment(
            @PathVariable("assignment_id") Integer assignment_id,
            @RequestPart("files") List<MultipartFile> files,
            @AuthenticationPrincipal User user
    ) {
        return assignmentSubmisstionService.submitAssignment(assignment_id, files, user.getUsername());
    }

    // This method is used to grade an assignment
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @PutMapping("/{student_id}/grade")
    public AssignmentSubmission gradeAssignment(
            @PathVariable("assignment_id") Integer assignment_id,
            @PathVariable("student_id") String student_id,
            @RequestBody AssignmentSubmissionDto assignmentSubmissionDto,
            @AuthenticationPrincipal User user
    ) {
        return assignmentSubmisstionService.gradeAssignment(assignment_id, student_id, assignmentSubmissionDto, user.getUsername());
    }
}