package AdvSe.LMS.users.entities;

import AdvSe.LMS.cloudinary.CloudinaryFile;
import AdvSe.LMS.courses.entities.Course;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@DiscriminatorValue("STUDENT")
public class Student extends User {

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "student_courses", // Name of the join table
            joinColumns = @JoinColumn(name = "student_id"), // Foreign key in join table for Student
            inverseJoinColumns = @JoinColumn(name = "course_id") // Foreign key in join table for Course
    )
    @JsonManagedReference
    private List<Course> courses = new ArrayList<>();

    public Student(String id, String name, String password, String email, String phone, CloudinaryFile profilePicture, List<Course> courses) {
        super(id, name, password, email, phone, profilePicture);
        this.courses = courses;
    }

    public void addCourse(Course course) {
        courses.add(course);
    }

    public void removeCourse(Course course) {
        courses.remove(course);
    }
}
