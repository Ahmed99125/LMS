package AdvSe.LMS.courses.entities.Questions.Submissions;

import AdvSe.LMS.courses.entities.Questions.Quiz;
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
@Table(name = "quiz_submissions")
public class QuizSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @OneToMany(mappedBy = "quizSubmission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionAnswer> answers = new ArrayList<>();

    private Double score;

    public void addAnswer(QuestionAnswer questionAnswer) {
        answers.add(questionAnswer);
    }

    public void removeAnswer(QuestionAnswer questionAnswer) {
        answers.remove(questionAnswer);
    }
}
