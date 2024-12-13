package AdvSe.LMS.users.entities;

import AdvSe.LMS.courses.entities.Course;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
public class Student extends User {

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "student_courses", // Name of the join table
            joinColumns = @JoinColumn(name = "student_id"), // Foreign key in join table for Student
            inverseJoinColumns = @JoinColumn(name = "course_id") // Foreign key in join table for Course
    )
    private List<Course> courses = new ArrayList<>();

    public void addCourse(Course course) {
        courses.add(course);
        course.addStudent(this);
    }

    public void removeCourse(Course course) {
        courses.remove(course);
        course.removeStudent(this);
    }

    public List<Course> getCourses() {
        return courses;
    }
}
