package AdvSe.LMS.courses.controllers;

import AdvSe.LMS.courses.dtos.CreateQuestionDto;
import AdvSe.LMS.courses.dtos.UpdateQuestionDto;
import AdvSe.LMS.courses.entities.Questions.Question;
import AdvSe.LMS.courses.services.QuestionsService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/courses/{course_id}/questions")
public class QuestionsController {
    private final QuestionsService questionsService;

    public QuestionsController(QuestionsService questionsService) {
        this.questionsService = questionsService;
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @GetMapping("")
    List<Question> getQuestions(
            @PathVariable("course_id") Integer course_id,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user
    ) {
        return questionsService.getQuestionsByCourseId(course_id, user.getUsername());
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @GetMapping("/{question_id}")
    Question getQuestionById(
            @PathVariable("course_id") Integer course_id,
            @PathVariable("question_id") Integer question_id,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user
    ) {
        return questionsService.getQuestionById(course_id, question_id, user.getUsername());
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    Question postQuestion(
            @PathVariable("course_id") Integer course_id,
            @Valid @RequestBody CreateQuestionDto createQuestionDto,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user
    ) {
        return questionsService.createQuestion(course_id, createQuestionDto, user.getUsername());
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @PutMapping("/{question_id}")
    Question updateQuestion(
            @PathVariable("course_id") Integer course_id,
            @PathVariable("question_id") Integer question_id,
            @Valid @RequestBody UpdateQuestionDto updateQuestionDto,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user
    ) {
        return questionsService.updateQuestion(course_id, question_id, updateQuestionDto, user.getUsername());
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @DeleteMapping("/{question_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteQuestion(
            @PathVariable("course_id") Integer course_id,
            @PathVariable("question_id") Integer question_id,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user
    ) {
        questionsService.deleteQuestion(course_id, question_id, user.getUsername());
    }
}
