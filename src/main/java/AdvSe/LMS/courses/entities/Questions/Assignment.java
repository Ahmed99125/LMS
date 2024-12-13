package AdvSe.LMS.courses.entities.Questions;

import AdvSe.LMS.courses.entities.Course;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "assignments")
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String data;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;


    public Assignment() {
    }
    public Assignment(Integer id, String name, String data, Course course) {
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
}