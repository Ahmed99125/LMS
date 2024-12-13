package AdvSe.LMS.courses.entities.Lessons;

import AdvSe.LMS.courses.entities.Course;
import AdvSe.LMS.users.entities.Student;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lessons")
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String data;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "lesson_attendance",
            joinColumns = @JoinColumn(name = "lesson_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private List<Student> attendance = new ArrayList<>();

    public Lesson() {
    }
    public Lesson(Integer id, String name, String data, Course course) {
        this.id = id;
        this.name = name;
        this.data = data;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public List<Student> getAttendance() {
        return attendance;
    }

    public void addStudent(Student student) {
        attendance.add(student);
    }

    public void removeStudent(Student student) {
        attendance.remove(student);
    }
}
