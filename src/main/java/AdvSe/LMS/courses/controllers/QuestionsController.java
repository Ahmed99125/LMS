package AdvSe.LMS.courses.controllers;

import AdvSe.LMS.courses.dtos.CreateQuestionDto;
import AdvSe.LMS.courses.dtos.UpdateQuestionDto;
import AdvSe.LMS.courses.entities.Questions.Question;
import AdvSe.LMS.courses.repositories.QuestionsRepository;
import AdvSe.LMS.courses.services.QuestionsService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses/{course_id}/questions")
public class QuestionsController {
    private final QuestionsService questionsService;
    private final QuestionsRepository questionsRepository;

    public QuestionsController(QuestionsService questionsService, QuestionsRepository questionsRepository) {
        this.questionsService = questionsService;
        this.questionsRepository = questionsRepository;
    }

    @GetMapping("")
    List<Question> getQuestions(@PathVariable("course_id") Integer course_id) {
        return questionsService.getQuestionsByCourseId(course_id);
    }

    @GetMapping("/{question_id}")
    Question getQuestionById(
            @PathVariable("course_id") Integer course_id,
            @PathVariable("question_id") Integer question_id) {
        return questionsService.getQuestionById(course_id, question_id);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    Question postQuestion(
            @PathVariable("course_id") Integer course_id,
            @RequestBody CreateQuestionDto createQuestionDto
    ) {
        return questionsService.createQuestion(course_id, createQuestionDto);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @PutMapping("/{question_id}")
    Question updateQuestion(
            @PathVariable("course_id") Integer course_id,
            @PathVariable("question_id") Integer question_id,
            @RequestBody UpdateQuestionDto updateQuestionDto) {
        return questionsService.updateQuestion(course_id, question_id, updateQuestionDto);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @DeleteMapping("/{question_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteQuestion(
            @PathVariable("course_id") Integer course_id,
            @PathVariable("question_id") Integer question_id) {
        Question question = questionsService.getQuestionById(course_id, question_id);
        questionsRepository.delete(question);
    }
}
