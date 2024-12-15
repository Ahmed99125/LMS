package AdvSe.LMS.users.entities;

import AdvSe.LMS.cloudinary.CloudinaryFile;
import AdvSe.LMS.courses.entities.Course;
import AdvSe.LMS.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
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

    public Student(String id, String name, String password, Role role, String email, String phone, CloudinaryFile profilePicture, List<Course> courses) {
        super(id, name, password, role, email, phone, profilePicture);
        this.courses = courses;
    }

    public void addCourse(Course course) {
        courses.add(course);
        course.addStudent(this);
    }

    public void removeCourse(Course course) {
        courses.remove(course);
        course.removeStudent(this);
    }
}
