package AdvSe.LMS.courses.repositories;

import AdvSe.LMS.cloudinary.CloudinaryFile;
import AdvSe.LMS.courses.entities.Course;
import AdvSe.LMS.courses.entities.Questions.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.expression.spel.ast.Assign;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentsRepository extends JpaRepository<Assignment, Integer> {
}