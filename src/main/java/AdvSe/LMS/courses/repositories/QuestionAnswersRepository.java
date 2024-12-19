package AdvSe.LMS.courses.repositories;

import AdvSe.LMS.courses.entities.Questions.Submissions.QuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionAnswersRepository extends JpaRepository<QuestionAnswer, Integer> {
}
