package AdvSe.LMS.users.repositories;

import AdvSe.LMS.users.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentsRepository extends JpaRepository<Student, String> {
}
