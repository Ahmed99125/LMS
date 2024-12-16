package AdvSe.LMS.users.services;

import AdvSe.LMS.users.dtos.CreateUserDto;
import AdvSe.LMS.users.dtos.UpdateProfileDto;
import AdvSe.LMS.users.entities.Admin;
import AdvSe.LMS.users.entities.Student;
import AdvSe.LMS.users.repositories.StudentsRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class StudentsService {

    private final UsersService usersService;
    private final StudentsRepository studentsRepository;

    public StudentsService(UsersService usersService, StudentsRepository studentsRepository) {
        this.usersService = usersService;
        this.studentsRepository = studentsRepository;
    }

    public List<Student> getStudents() {
        return studentsRepository.findAll();
    }

    public Student createStudent(CreateUserDto userDto) {
        Student student = new Student();
        usersService.setUserData(student, userDto);
        return studentsRepository.save(student);
    }

    public Student updateStudent(String studentId, CreateUserDto userDto) {
        Student student = studentsRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Student not found"));
        usersService.updateUser(student, userDto);
        return studentsRepository.save(student);
    }

    public Student getStudentById(String studentId) {
        return studentsRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Student not found"));
    }

    public void deleteStudent(String studentId) {
        if (!studentsRepository.existsById(studentId))
            throw new ResponseStatusException(NOT_FOUND, "Student not found");
        studentsRepository.deleteById(studentId);
    }
    
    public Student updateProfile(String id, UpdateProfileDto profileDto) {
        Student user = studentsRepository.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Admin not found"));
        
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setEmail(profileDto.getEmail());
        user.setPassword(passwordEncoder.encode(profileDto.getPassword()));
        user.setPhone(profileDto.getPhone());
        user.setProfilePicture(profileDto.getProfilePicture());

        return studentsRepository.save(user);
    }
}
