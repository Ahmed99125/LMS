package AdvSe.LMS.courses.repositories;

import AdvSe.LMS.courses.entities.Questions.Submissions.AssignmentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentSubmissionsRepository extends JpaRepository<AssignmentSubmission, Integer> {
}
