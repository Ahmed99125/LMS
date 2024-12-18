package AdvSe.LMS.courses.repositories;

import AdvSe.LMS.courses.entities.Questions.Question;
import AdvSe.LMS.courses.entities.Questions.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizzesRepository extends JpaRepository<Quiz, Integer> {
}
