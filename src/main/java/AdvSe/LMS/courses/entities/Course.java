package AdvSe.LMS.courses.entities;

import AdvSe.LMS.courses.entities.Lessons.Lesson;
import AdvSe.LMS.courses.entities.Questions.Assignment;
import AdvSe.LMS.courses.entities.Questions.Question;
import AdvSe.LMS.courses.entities.Questions.Quiz;
import AdvSe.LMS.users.entities.Instructor;
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
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    private String description;

    private String courseCode;

    @ManyToOne
    @JoinColumn(name = "Instructor_id", nullable = false)
    private Instructor instructor;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lesson> lessons = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Quiz> quizzes = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Assignment> assignments = new ArrayList<>();

    @ManyToMany(mappedBy = "courses")
    private List<Student> students = new ArrayList<>();



    public void addStudent(Student student) {
        students.add(student);
    }

    public void removeStudent(Student student) {
        students.remove(student);
    }

    public void addLesson(Lesson lesson) {
        lessons.add(lesson);
        lesson.setCourse(this);
    }

    public void removeLesson(Lesson lesson) {
        lessons.remove(lesson);
    }

    public void addQuestion(Question question) {
        questions.add(question);
        question.setCourse(this);
    }

    public void removeQuestion(Question question) {
        questions.remove(question);
    }

    public void addQuiz(Quiz quiz) {
        quizzes.add(quiz);
        quiz.setCourse(this);
    }

    public void removeQuiz(Quiz quiz) {
        quizzes.remove(quiz);
    }

    public void addAssignment(Assignment assignment) {
        assignments.add(assignment);
        assignment.setCourse(this);
    }

    public void removeAssignment(Assignment assignment) {
        assignments.remove(assignment);
    }
}
