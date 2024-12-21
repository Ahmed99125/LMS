package AdvSe.LMS.courses.repositories;

import AdvSe.LMS.courses.entities.Questions.Submissions.AssignmentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssignmentSubmissionsRepository extends JpaRepository<AssignmentSubmission, Integer> {
    Optional<AssignmentSubmission> findByAssignmentIdAndStudentId(Integer assignmentId, String studentId);
}
