package AdvSe.LMS.cloudinary;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CloudinaryRepository extends JpaRepository<CloudinaryFile, String> {
}
