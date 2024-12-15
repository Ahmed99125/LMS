package AdvSe.LMS.courses.repositories;

import AdvSe.LMS.courses.entities.Questions.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface AssignmentsRepository extends JpaRepository<Assignment, Integer> {
}