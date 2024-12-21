package AdvSe.LMS.courses.dtos;

import AdvSe.LMS.utils.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateQuestionDto {
    private Integer questionId;
    private QuestionType type;
    private String question;
    private String answer;
    private String instructorId;
}
