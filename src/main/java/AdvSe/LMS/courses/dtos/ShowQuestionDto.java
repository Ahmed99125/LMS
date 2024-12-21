package AdvSe.LMS.courses.dtos;

import AdvSe.LMS.courses.entities.Questions.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShowQuestionDto {
    private Integer id;
    private String question;
    private String answer;

    public ShowQuestionDto(Question question) {
        this.id = question.getId();
        this.question = question.getQuestion();
        this.answer = question.getAnswer();
    }
}
