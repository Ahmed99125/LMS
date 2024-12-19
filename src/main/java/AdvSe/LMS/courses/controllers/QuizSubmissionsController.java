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
@RequestMapping("/api/courses/{course_id}/quizzes/{quiz_id}/submissions")
public class QuizSubmissionsController {

    private final QuizSubmissionsService quizSubmissionsService;

    public QuizSubmissionsController(QuizSubmissionsService quizSubmissionsService) {
        this.quizSubmissionsService = quizSubmissionsService;
    }

    // This method is used to get all the submissions for a particular quiz
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @GetMapping("")
    public List<QuizSubmission> getQuizSubmissions(
            @PathVariable("course_id") Integer course_id,
            @PathVariable("quiz_id") Integer quiz_id,
            @AuthenticationPrincipal User user
    ) {
        return quizSubmissionsService.getQuizSubmissions(course_id, quiz_id, user.getUsername());
    }

    // This method is used to get the submission of a particular student for a particular quiz
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @GetMapping("/{student_id}")
    public QuizSubmission getQuizSubmission(
            @PathVariable("course_id") Integer course_id,
            @PathVariable("quiz_id") Integer quiz_id,
            @PathVariable("student_id") String student_id,
            @AuthenticationPrincipal User user
    ) {
        return quizSubmissionsService.getQuizSubmission(course_id, quiz_id, student_id, user.getUsername());
    }

    // This method is used to get the submission of the current student for a particular quiz
    @PreAuthorize("hasAuthority('STUDENT')")
    @GetMapping("/my_submission")
    public QuizSubmission getMyQuizSubmission(
            @PathVariable("course_id") Integer course_id,
            @PathVariable("quiz_id") Integer quiz_id,
            @AuthenticationPrincipal User user
    ) {
        return quizSubmissionsService.getMyQuizSubmission(course_id, quiz_id, user.getUsername());
    }

    // This method is used to submit a quiz
    @PreAuthorize("hasAuthority('STUDENT')")
    @PostMapping("")
    public QuizSubmission submitQuiz(
            @PathVariable("course_id") Integer course_id,
            @PathVariable("quiz_id") Integer quiz_id,
            @RequestBody QuizSubmissionDto quizSubmissionDto,
            @AuthenticationPrincipal User user
    ) {
        quizSubmissionDto.setCourseId(course_id);
        quizSubmissionDto.setQuizId(quiz_id);
        quizSubmissionDto.setStudentId(user.getUsername());
        return quizSubmissionsService.submitQuiz(quizSubmissionDto);
    }
}
