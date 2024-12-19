package AdvSe.LMS.courses.dtos.submissions;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmissionDto {
    private Integer courseId;
    private Integer quizId;
    private String studentId;
    @NotNull
    private List<QuestionSubmissionDto> answers;
}
