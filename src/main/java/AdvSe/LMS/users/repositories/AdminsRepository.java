package AdvSe.LMS.users.repositories;

import AdvSe.LMS.users.entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminsRepository extends JpaRepository<Admin, String> {
	
}
