package AdvSe.LMS.courses.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCourseDto {
    private String name;
    private String description;
    private String courseCode;
    private String instructorId;
}