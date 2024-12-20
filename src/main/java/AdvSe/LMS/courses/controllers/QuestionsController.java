package AdvSe.LMS.courses.controllers;

import AdvSe.LMS.courses.dtos.CreateQuestionDto;
import AdvSe.LMS.courses.dtos.ShowQuestionDto;
import AdvSe.LMS.courses.dtos.UpdateQuestionDto;
import AdvSe.LMS.courses.entities.Questions.Question;
import AdvSe.LMS.courses.services.QuestionsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionsController {
    private final QuestionsService questionsService;

    public QuestionsController(QuestionsService questionsService) {
        this.questionsService = questionsService;
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @GetMapping("")
    List<ShowQuestionDto> getQuestions(
            @RequestParam Integer courseId,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user
    ) {
        List<Question> questions = questionsService.getQuestionsByCourseId(courseId, user.getUsername());
        List<ShowQuestionDto> showedQuestions = new ArrayList<>();
        for (Question question : questions) {
            showedQuestions.add(new ShowQuestionDto(question));
        }
        return showedQuestions;
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @GetMapping("/{questionId}")
    ShowQuestionDto getQuestionById(
            @PathVariable("questionId") Integer questionId,
            @AuthenticationPrincipal User user
    ) {
        return new ShowQuestionDto(questionsService.getQuestionById(questionId, user.getUsername()));
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    ShowQuestionDto postQuestion(
            @Valid @RequestBody CreateQuestionDto createQuestionDto,
            @AuthenticationPrincipal User user
    ) {
        createQuestionDto.setInstructorId(user.getUsername());
        return new ShowQuestionDto(questionsService.createQuestion(createQuestionDto));
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @PutMapping("/{questionId}")
    ShowQuestionDto updateQuestion(
            @PathVariable("questionId") Integer questionId,
            @Valid @RequestBody UpdateQuestionDto updateQuestionDto,
            @AuthenticationPrincipal User user
    ) {
        updateQuestionDto.setQuestionId(questionId);
        updateQuestionDto.setInstructorId(user.getUsername());
        return new ShowQuestionDto(questionsService.updateQuestion(updateQuestionDto));
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    @DeleteMapping("/{questionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteQuestion(
            @PathVariable("questionId") Integer questionId,
            @AuthenticationPrincipal User user
    ) {
        questionsService.deleteQuestion(questionId, user.getUsername());
    }
}
