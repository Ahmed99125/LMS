package AdvSe.LMS.courses.controllers;

import AdvSe.LMS.courses.dtos.submissions.QuizSubmissionDto;
import AdvSe.LMS.courses.entities.Questions.Submissions.QuizSubmission;
import AdvSe.LMS.courses.services.QuizSubmissionsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes/{quizId}/submissions")
public class QuizSubmissionsController {

    private final QuizSubmissionsService quizSubmissionsService;

    public QuizSubmissionsController(QuizSubmissionsService quizSubmissionsService) {
        this.quizSubmissionsService = quizSubmissionsService;
    }

    // This method is used to get all the submissions for a particular quiz
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @GetMapping("")
    public List<QuizSubmission> getQuizSubmissions(
            @PathVariable("quizId") Integer quizId,
            @AuthenticationPrincipal User user
    ) {
        return quizSubmissionsService.getQuizSubmissions(quizId, user.getUsername());
    }

    // This method is used to get the submission of a particular student for a particular quiz
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @GetMapping("/{studentId}")
    public QuizSubmission getQuizSubmission(
            @PathVariable("quizId") Integer quizId,
            @PathVariable("studentId") String studentId,
            @AuthenticationPrincipal User user
    ) {
        return quizSubmissionsService.getQuizSubmissionByStudentId(quizId, studentId, user.getUsername());
    }

    // This method is used to get the submission of the current student for a particular quiz
    @PreAuthorize("hasAuthority('STUDENT')")
    @GetMapping("/my_submission")
    public QuizSubmission getMyQuizSubmission(
            @PathVariable("quizId") Integer quizId,
            @AuthenticationPrincipal User user
    ) {
        return quizSubmissionsService.getMyQuizSubmission(quizId, user.getUsername());
    }

    // This method is used to submit a quiz
    @PreAuthorize("hasAuthority('STUDENT')")
    @PostMapping("")
    public QuizSubmission submitQuiz(
            @ModelAttribute QuizSubmissionDto quizSubmissionDto,
            @AuthenticationPrincipal User user
    ) {
        quizSubmissionDto.setStudentId(user.getUsername());
        return quizSubmissionsService.submitQuiz(quizSubmissionDto);
    }
}
