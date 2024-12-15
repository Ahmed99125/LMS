package AdvSe.LMS.users.entities;

import AdvSe.LMS.cloudinary.CloudinaryFile;
import AdvSe.LMS.courses.entities.Course;
import AdvSe.LMS.utils.enums.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "instructors")
public class Instructor extends User {

    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Course> courses = new ArrayList<>();

    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    public Instructor(String id, String name, String password, Role role, String email, String phone, CloudinaryFile profilePicture, List<Course> courses) {
        super(id, name, password, role, email, phone, profilePicture);
        this.courses = courses;
    }

    public void addCourse(Course course) {
        courses.add(course);
        course.setInstructor(this);
    }

    public void removeCourse(Course course) {
        courses.remove(course);
    }

    public void addNotification(Notification notification) {
        notifications.add(notification);
    }

    public void removeNotification(Notification notification) {
        notifications.remove(notification);
    }
}
