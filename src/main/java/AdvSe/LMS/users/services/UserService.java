package AdvSe.LMS.users.services;

import AdvSe.LMS.cloudinary.CloudinaryFile;
import AdvSe.LMS.cloudinary.CloudinaryService;
import AdvSe.LMS.users.dtos.CreateUserDto;
import AdvSe.LMS.users.entities.Student;
import AdvSe.LMS.users.repositories.StudentsRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final StudentsRepository studentsRepository;
    private final CloudinaryService cloudinaryService;

    public UserService(StudentsRepository studentsRepository, CloudinaryService cloudinaryService) {
        this.studentsRepository = studentsRepository;
        this.cloudinaryService = cloudinaryService;
    }

    public Student createStudent(CreateUserDto studentDto) {
        Student student = new Student();
        student.setId(studentDto.getId());
        student.setName(studentDto.getName());
        student.setPassword(studentDto.getPassword());
        student.setRole(studentDto.getRole());
        try {
            CloudinaryFile profilePicture = cloudinaryService.uploadFile(studentDto.getImage(), "profile_pictures");
            student.setProfilePicture(profilePicture);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return studentsRepository.save(student);
    }
}
