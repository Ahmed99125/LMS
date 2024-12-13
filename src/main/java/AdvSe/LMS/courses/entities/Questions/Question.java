package AdvSe.LMS.courses.entities.Questions;

import AdvSe.LMS.courses.entities.Course;
import AdvSe.LMS.users.entities.Instructor;
import jakarta.persistence.*;

@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private QuestionType type;
    private String question;
    private String answer;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    public Question() {
    }
    public Question(Integer id, QuestionType type, String question, String answer, Course course) {
        this.id = id;
        this.type = type;
        this.question = question;
        this.answer = answer;
        this.course = course;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public QuestionType getType() {
        return type;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
