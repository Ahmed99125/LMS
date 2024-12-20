package AdvSe.LMS.courses.dtos.submissions;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class AssignmentFeedbackDto {
    @NotNull
    private Integer assignmentId;
    @NotBlank
    private String studentId;
    @NotNull
    @Min(0)
    @Max(100)
    private Double score;
    private String feedback;
    private String instructorId;
}
