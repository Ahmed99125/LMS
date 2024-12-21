package AdvSe.LMS.courses.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizDto {
    @NotBlank
    private Integer courseId;
    @NotBlank
    private String name;
    @NotNull
    private Set<Integer> questionIds;
    private String instructorId;
}
