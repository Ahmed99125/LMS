package AdvSe.LMS.courses.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateAssignmentDto {
    @NotBlank
    private Integer courseId;
    @NotBlank
    private String name;
    @NotNull
    private List<MultipartFile> files;
    private String instructorId;
}
