package AdvSe.LMS.courses.repositories;

import AdvSe.LMS.courses.entities.Questions.Submissions.QuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuizSubmissionsRepository extends JpaRepository<QuizSubmission, Integer> {
    Optional<QuizSubmission> findByQuizIdAndStudentId(Integer quizId, String studentId);
}
