package AdvSe.LMS.courses.dtos;

import AdvSe.LMS.utils.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

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