package AdvSe.LMS.courses.entities.Questions.Submissions;

import AdvSe.LMS.courses.entities.Questions.Quiz;
import AdvSe.LMS.users.entities.Student;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Table(
        name = "quiz_submissions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "quiz_id"})
)
public class QuizSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    @JsonManagedReference
    private Student student;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    @JsonBackReference
    private Quiz quiz;

    @OneToMany(mappedBy = "quizSubmission", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<QuestionAnswer> answers = new ArrayList<>();

    private Double score;

    public void addAnswer(QuestionAnswer questionAnswer) {
        answers.add(questionAnswer);
    }

    public void removeAnswer(QuestionAnswer questionAnswer) {
        answers.remove(questionAnswer);
    }
}
