package AdvSe.LMS.courses.entities.Questions;

import AdvSe.LMS.courses.entities.Course;
import AdvSe.LMS.users.entities.Student;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quizzes")
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "quiz_questions",
            joinColumns = @JoinColumn(name = "quiz_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    private List<Question> questions = new ArrayList<>();

    public Quiz() {
    }
    public Quiz(Integer id, String name, Course course) {
        this.id = id;
        this.name = name;
        this.course = course;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void addQuestion(Question question) {
        questions.add(question);
    }

    public void removeQuestion(Question question) {
        questions.remove(question);
    }
}
