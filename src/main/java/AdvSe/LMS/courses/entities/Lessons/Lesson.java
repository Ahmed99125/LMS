package AdvSe.LMS.courses.entities.Lessons;

import AdvSe.LMS.cloudinary.CloudinaryFile;
import AdvSe.LMS.courses.entities.Course;
import AdvSe.LMS.users.entities.Student;
import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(name = "lessons")
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    @JsonBackReference
    private Course course;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "lesson_files",
            joinColumns = @JoinColumn(name = "lesson_id"),
            inverseJoinColumns = @JoinColumn(name = "file_id")
    )
    private List<CloudinaryFile> lessonFiles = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "lesson_attendance",
            joinColumns = @JoinColumn(name = "lesson_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private List<Student> attendance = new ArrayList<>();

    public void addStudent(Student student) {
        attendance.add(student);
    }

    public void removeStudent(Student student) {
        attendance.remove(student);
    }

    public void addLessonFile(CloudinaryFile lessonFile) {
        lessonFiles.add(lessonFile);
    }

    public void removeLessonFile(CloudinaryFile lessonFile) {
        lessonFiles.remove(lessonFile);
    }

    public void removeAllLessonFiles() {
        lessonFiles.clear();
    }
}
