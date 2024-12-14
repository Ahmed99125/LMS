package AdvSe.LMS.courses.entities.Questions;

import AdvSe.LMS.cloudinary.CloudinaryFile;
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

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "assignment_files",
            joinColumns = @JoinColumn(name = "assignment_id"),
            inverseJoinColumns = @JoinColumn(name = "file_id")
    )
    private List<CloudinaryFile> assignmentFiles = new ArrayList<>();


    public Assignment() {
    }

    public Assignment(Integer id, String name, Course course) {
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

    public List<CloudinaryFile> getAssignmentFiles() {
        return assignmentFiles;
    }

    public void addAssignmentFile(CloudinaryFile assignmentFile) {
        assignmentFiles.add(assignmentFile);
    }

    public void setAssignmentFiles(List<CloudinaryFile> assignmentFiles) {
        this.assignmentFiles = assignmentFiles;
    }

    public void removeAssignmentFile(CloudinaryFile assignmentFile) {
        assignmentFiles.remove(assignmentFile);
    }
}