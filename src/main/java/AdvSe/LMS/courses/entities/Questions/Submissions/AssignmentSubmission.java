package AdvSe.LMS.courses.entities.Questions.Submissions;

import AdvSe.LMS.cloudinary.CloudinaryFile;
import AdvSe.LMS.courses.entities.Questions.Assignment;
import AdvSe.LMS.users.entities.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "assignment_submissions")
public class AssignmentSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "submission_files",
            joinColumns = @JoinColumn(name = "submission_id"),
            inverseJoinColumns = @JoinColumn(name = "file_id")
    )
    private List<CloudinaryFile> submissionFiles = new ArrayList<>();

    public void addSubmissionFile(CloudinaryFile assignmentFile) {
        submissionFiles.add(assignmentFile);
    }

    public void removeSubmissionFile(CloudinaryFile assignmentFile) {
        submissionFiles.remove(assignmentFile);
    }
}