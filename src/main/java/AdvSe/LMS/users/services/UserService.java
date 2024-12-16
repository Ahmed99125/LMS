package AdvSe.LMS.users.services;

import AdvSe.LMS.users.dtos.CreateUserDto;
import AdvSe.LMS.users.entities.Admin;
import AdvSe.LMS.users.entities.Instructor;
import AdvSe.LMS.users.entities.Student;
import AdvSe.LMS.users.repositories.AdminsRepository;
import AdvSe.LMS.users.repositories.InstructorsRepository;
import AdvSe.LMS.users.repositories.StudentsRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    private final StudentsRepository studentsRepository;
    private final InstructorsRepository instructorsRepository;
    private final AdminsRepository adminsRepository;

    public UserService(StudentsRepository studentsRepository, InstructorsRepository instructorsRepository, AdminsRepository adminsRepository) {
        this.studentsRepository = studentsRepository;
        this.instructorsRepository = instructorsRepository;
        this.adminsRepository = adminsRepository;
    }

    // Add the hashed password instead at the end, this is just for the ease of testing

    public Student createStudent(CreateUserDto userDto) {
        Student user = new Student();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(userDto.getPassword());
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setPassword(userDto.getPassword());
        user.setRole(userDto.getRole());
        return studentsRepository.save(user);
    }

    public Instructor createInstructor(CreateUserDto userDto) {
        Instructor user = new Instructor();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(userDto.getPassword());
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setPassword(userDto.getPassword());
        user.setRole(userDto.getRole());
        return instructorsRepository.save(user);
    }

    public Admin createAdmin(CreateUserDto userDto) {
        Admin user = new Admin();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(userDto.getPassword());
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setPassword(userDto.getPassword());
        user.setRole(userDto.getRole());
        return adminsRepository.save(user);
    }

    public String updateUser(String oldId, CreateUserDto userDto) {
        // Check the role from the incoming DTO
        switch (userDto.getRole()) {
            case ADMIN:
                return updateAdmin(oldId, userDto);
            case STUDENT:
                return updateStudent(oldId, userDto);
            case INSTRUCTOR:
                return updateInstructor(oldId, userDto);
            default:
                return "Invalid data";
        }
    }

    private String updateAdmin(String oldId, CreateUserDto userDto) {
        Admin existingAdmin = adminsRepository.findById(oldId).orElse(null);
        if (existingAdmin == null) {
            return "not found";
        }

        // Update fields
        adminsRepository.deleteById(oldId); // Remove the old record
        Admin updatedAdmin = new Admin();
        updatedAdmin.setId(userDto.getId());
        updatedAdmin.setName(userDto.getName());
        updatedAdmin.setPassword(userDto.getPassword());
        adminsRepository.save(updatedAdmin); // Save the updated entity
        return "updated";
    }

    private String updateStudent(String oldId, CreateUserDto userDto) {
        Student existingStudent = studentsRepository.findById(oldId).orElse(null);
        if (existingStudent == null) {
            return "not found";
        }

        // Update fields
        studentsRepository.deleteById(oldId); // Remove the old record
        Student updatedStudent = new Student();
        updatedStudent.setId(userDto.getId());
        updatedStudent.setName(userDto.getName());
        updatedStudent.setPassword(userDto.getPassword());
        studentsRepository.save(updatedStudent); // Save the updated entity
        return "updated";
    }

    private String updateInstructor(String oldId, CreateUserDto userDto) {
        Instructor existingInstructor = instructorsRepository.findById(oldId).orElse(null);
        if (existingInstructor == null) {
            return "not found";
        }

        // Update fields
        instructorsRepository.deleteById(oldId); // Remove the old record
        Instructor updatedInstructor = new Instructor();
        updatedInstructor.setId(userDto.getId());
        updatedInstructor.setName(userDto.getName());
        updatedInstructor.setPassword(userDto.getPassword());
        instructorsRepository.save(updatedInstructor); // Save the updated entity
        return "updated";
    }

    public String deleteUser(String id) {
        if (adminsRepository.existsById(id)) {
            adminsRepository.deleteById(id);
            return "deleted";
        } else if (instructorsRepository.existsById(id)) {
            instructorsRepository.deleteById(id);
            return "deleted";
        } else if (studentsRepository.existsById(id)) {
            studentsRepository.deleteById(id);
            return "deleted";
        }
        return "not found";
    }
}
