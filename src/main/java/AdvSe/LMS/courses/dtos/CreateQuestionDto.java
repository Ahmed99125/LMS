package AdvSe.LMS.courses.dtos;

import AdvSe.LMS.utils.enums.QuestionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuestionDto {
    @NotNull
    private QuestionType type;
    @NotBlank
    private String question;
    @NotBlank
    private String answer;
}
