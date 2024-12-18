package AdvSe.LMS.users.repositories;

import AdvSe.LMS.users.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<User, String> {
}
