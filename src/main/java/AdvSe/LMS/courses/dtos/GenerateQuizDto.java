package AdvSe.LMS.courses.dtos;

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
public class GenerateQuizDto {
    @NotBlank
    private Integer courseId;
    @NotBlank
    private String name;
    @NotNull
    private Integer numberOfQuestions;
    private String instructorId;
}
