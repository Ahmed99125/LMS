package AdvSe.LMS.courses.dtos;

import AdvSe.LMS.courses.entities.Course;
import AdvSe.LMS.courses.entities.Lessons.Lesson;
import AdvSe.LMS.courses.entities.Questions.Assignment;
import AdvSe.LMS.courses.entities.Questions.Question;
import AdvSe.LMS.courses.entities.Questions.Quiz;
import AdvSe.LMS.users.entities.Student;
import AdvSe.LMS.users.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto {
    private Integer id;

    private String name;

    private String description;

    private String courseCode;

    private User instructor;

    private List<Lesson> lessons;

    private List<Question> questions;

    private List<Quiz> quizzes;

    private List<Assignment> assignments;

    private List<User> students;

    public CourseDto(Course course) {
        this.id = course.getId();
        this.name = course.getName();
        this.description = course.getDescription();
        this.courseCode = course.getCourseCode();
        this.instructor = new User(course.getInstructor());
        this.students = getUserStudents(course.getStudents());
        this.lessons = course.getLessons();
        this.questions = course.getQuestions();
        this.quizzes = course.getQuizzes();
        this.assignments = course.getAssignments();
    }

    public static List<CourseDto> fromList(List<Course> all) {
        return all.stream().map(CourseDto::new).toList();
    }

    private List<User> getUserStudents(List<Student> students) {
        return students.stream().map(User::new).toList();
    }
}
