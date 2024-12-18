package AdvSe.LMS.courses.controllers;

import AdvSe.LMS.courses.dtos.GenerateQuizDto;
import AdvSe.LMS.courses.dtos.QuizDto;
import AdvSe.LMS.courses.entities.Questions.Quiz;
import AdvSe.LMS.courses.services.QuizzesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses/{course_id}/quizzes")
public class QuizzesController {
    private final QuizzesService quizzesService;

    public QuizzesController(QuizzesService quizzesService) {
        this.quizzesService = quizzesService;
    }

    @GetMapping("")
    List<Quiz> getQuizzes(@PathVariable("course_id") Integer course_id) {
        return quizzesService.getQuestionsByCourseId(course_id);
    }

    @GetMapping("/{quiz_id}")
    Quiz getQuizById(
            @PathVariable("course_id") Integer course_id,
            @PathVariable("quiz_id") Integer quiz_id) {
        return quizzesService.getQuizById(course_id, quiz_id);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    Quiz postQuiz(
            @PathVariable("course_id") Integer course_id,
            @Valid @RequestBody QuizDto quizDto,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user
    ) {
        return quizzesService.createQuiz(course_id, quizDto, user.getUsername());
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @PostMapping("generate_quiz")
    @ResponseStatus(HttpStatus.CREATED)
    Quiz generateQuiz(
            @PathVariable("course_id") Integer course_id,
            @Valid @RequestBody GenerateQuizDto generateQuizDto,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user
    ) {
        return quizzesService.generateQuiz(course_id, generateQuizDto, user.getUsername());
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @DeleteMapping("/{quiz_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteQuestion(
            @PathVariable("course_id") Integer course_id,
            @PathVariable("quiz_id") Integer quiz_id,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user
    ) {
        quizzesService.deleteQuiz(course_id, quiz_id, user.getUsername());
    }
}
