package AdvSe.LMS.courses.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCourseDto {
    @NotBlank
    private String name;

    @NotBlank
    private String instructorId;

    private String description;
    private String courseCode;
}