package AdvSe.LMS.courses.repositories;

import AdvSe.LMS.courses.entities.Lessons.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonsRepository extends JpaRepository<Lesson, Integer> {
}