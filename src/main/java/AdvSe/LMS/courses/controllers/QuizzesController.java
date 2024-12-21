package AdvSe.LMS.courses.controllers;

import AdvSe.LMS.courses.dtos.GenerateQuizDto;
import AdvSe.LMS.courses.dtos.QuizDto;
import AdvSe.LMS.courses.entities.Questions.Quiz;
import AdvSe.LMS.courses.services.QuizzesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
public class QuizzesController {
    private final QuizzesService quizzesService;

    public QuizzesController(QuizzesService quizzesService) {
        this.quizzesService = quizzesService;
    }

    @GetMapping("")
    List<Quiz> getQuizzes(@RequestParam Integer courseId) {
        return quizzesService.getQuestionsByCourseId(courseId);
    }

    @GetMapping("/{quizId}")
    Quiz getQuizById(@PathVariable("quizId") Integer quizId) {
        return quizzesService.getQuizById(quizId);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    Quiz postQuiz(
            @Valid @RequestBody QuizDto quizDto,
            @AuthenticationPrincipal User user
    ) {
        quizDto.setInstructorId(user.getUsername());
        return quizzesService.createQuiz(quizDto);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @PostMapping("generate_quiz")
    @ResponseStatus(HttpStatus.CREATED)
    Quiz generateQuiz(
            @Valid @RequestBody GenerateQuizDto generateQuizDto,
            @AuthenticationPrincipal User user
    ) {
        generateQuizDto.setInstructorId(user.getUsername());
        return quizzesService.generateQuiz(generateQuizDto);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @DeleteMapping("/{quizId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteQuestion(
            @PathVariable("quizId") Integer quizId,
            @AuthenticationPrincipal User user
    ) {
        quizzesService.deleteQuiz(quizId, user.getUsername());
    }
}
