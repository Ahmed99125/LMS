package AdvSe.LMS.users.repositories;

import AdvSe.LMS.users.entities.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstructorsRepository extends JpaRepository<Instructor, String> {
	
}
