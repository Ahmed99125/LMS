package AdvSe.LMS.courses.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLessonDto {
    private Integer lessonId;
    private String name;
    private List<MultipartFile> files;
    private String instructorId;
}
