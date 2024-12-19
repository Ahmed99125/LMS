package AdvSe.LMS.courses.dtos.submissions;

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
public class QuestionSubmissionDto {
    @NotNull
    private Integer questionId;
    @NotBlank
    private String answer;
}
