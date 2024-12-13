package AdvSe.LMS.users.services;

import AdvSe.LMS.users.dtos.CreateUserDto;
import AdvSe.LMS.users.entities.Student;
import AdvSe.LMS.users.repositories.StudentsRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final StudentsRepository studentsRepository;

    public UserService(StudentsRepository studentsRepository) {
        this.studentsRepository = studentsRepository;
    }

    public Student createStudent(CreateUserDto studentDto) {
        Student student = new Student();
        student.setId(studentDto.getId());
        student.setName(studentDto.getName());
        student.setPassword(studentDto.getPassword());
        student.setRole(studentDto.getRole());
        return studentsRepository.save(student);
    }
}
