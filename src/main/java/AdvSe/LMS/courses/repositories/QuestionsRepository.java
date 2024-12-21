package AdvSe.LMS.courses.repositories;

import AdvSe.LMS.courses.entities.Questions.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionsRepository extends JpaRepository<Question, Integer> {
}