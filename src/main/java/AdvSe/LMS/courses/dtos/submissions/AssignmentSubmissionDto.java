package AdvSe.LMS.courses.dtos.submissions;

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
public class AssignmentSubmissionDto {
    @NotNull
    private Integer assignmentId;
    @NotNull
    private List<MultipartFile> files;
    private String studentId;
}
