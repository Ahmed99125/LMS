package AdvSe.LMS.courses.entities.Questions.Submissions;

import AdvSe.LMS.courses.entities.Questions.Question;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "question_answers")
public class QuestionAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    private String userAnswer;

    @ManyToOne
    @JoinColumn(name = "submission_id", nullable = false)
    private QuizSubmission quizSubmission;
}
