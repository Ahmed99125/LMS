package AdvSe.LMS.courses.repositories;

import AdvSe.LMS.courses.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoursesRepository extends JpaRepository<Course, Integer> {
}
