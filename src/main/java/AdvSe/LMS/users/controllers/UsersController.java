package AdvSe.LMS.users.controllers;

import AdvSe.LMS.users.dtos.CreateUserDto;
import AdvSe.LMS.users.entities.Student;
import AdvSe.LMS.users.repositories.StudentsRepository;
import AdvSe.LMS.users.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/users")
public class UsersController {
    private final UserService userService;
    private final StudentsRepository studentsRepository;

    public UsersController(UserService userService, StudentsRepository studentsRepository) {
        this.userService = userService;
        this.studentsRepository = studentsRepository;
    }

    @GetMapping("/students")
    public List<Student> getStudents() {
        return studentsRepository.findAll();
    }

    @PostMapping("/students")
    @ResponseStatus(HttpStatus.CREATED)
    public Student createStudent(@RequestBody CreateUserDto studentDto) {
        return userService.createStudent(studentDto);
    }
}
