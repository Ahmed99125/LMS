package AdvSe.LMS.users.controllers;

import AdvSe.LMS.users.dtos.CreateUserDto;
import AdvSe.LMS.users.dtos.UpdateProfileDto;
import AdvSe.LMS.users.entities.Admin;
import AdvSe.LMS.users.entities.Instructor;
import AdvSe.LMS.users.entities.Student;
import AdvSe.LMS.users.entities.User;
import AdvSe.LMS.users.services.AdminsService;
import AdvSe.LMS.users.services.InstructorsService;
import AdvSe.LMS.users.services.StudentsService;
import AdvSe.LMS.users.services.UsersService;
import AdvSe.LMS.utils.enums.Role;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("api/users")
public class UsersController {
    private final UsersService usersService;
    private final StudentsService studentsService;
    private final InstructorsService instructorsService;
    private final AdminsService adminsService;

    public UsersController(UsersService usersService, StudentsService studentsService, InstructorsService instructorService, AdminsService adminService) {
        this.usersService = usersService;
        this.studentsService = studentsService;
        this.instructorsService = instructorService;
        this.adminsService = adminService;
    }

    @GetMapping("/profile")
    public User getLoggedInUser(@AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        return usersService.getLoggedInUser(user.getUsername());
    }

    @PutMapping("/profile/update_profile")
    public User updateProfile(@RequestBody UpdateProfileDto profileDto, @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        String userId = user.getUsername();
        Role role = Role.valueOf(user.getAuthorities().iterator().next().getAuthority());

        return switch (role) {
            case STUDENT -> studentsService.updateProfile(userId, profileDto);
            case INSTRUCTOR -> instructorsService.updateProfile(userId, profileDto);
            default -> adminsService.updateProfile(userId, profileDto);
        };
    }

    @PutMapping("/profile/update_picture")
    public User updatePicture(@RequestParam("profilePicture") MultipartFile profilePicture, @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        String userId = user.getUsername();
        Role role = Role.valueOf(user.getAuthorities().iterator().next().getAuthority());

        return switch (role) {
            case STUDENT -> studentsService.updatePicture(userId, profilePicture);
            case INSTRUCTOR -> instructorsService.updatePicture(userId, profilePicture);
            default -> adminsService.updatePicture(userId, profilePicture);
        };
    }

    @GetMapping("")
    public List<User> getUsers() {
        return usersService.getUsers();
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public User register(@Valid @RequestBody CreateUserDto userDto) {
        return switch (userDto.getRole()) {
            case STUDENT -> studentsService.createStudent(userDto);
            case INSTRUCTOR -> instructorsService.createInstructor(userDto);
            default -> adminsService.createAdmin(userDto);
        };
    }

    @GetMapping("/students")
    public List<Student> getStudents() {
        return studentsService.getStudents();
    }

    @GetMapping("/instructors")
    public List<Instructor> getInstructors() {
        return instructorsService.getInstructors();
    }

    @GetMapping("/admins")
    public List<Admin> getAdmins() {
        return adminsService.getAdmins();
    }

    @PutMapping("/students/{id}")
    public Student updateStudent(@PathVariable String id, @Valid @RequestBody CreateUserDto userDto) {
        return studentsService.updateStudent(id, userDto);
    }

    @PutMapping("/instructors/{id}")
    public Instructor updateInstructor(@PathVariable String id, @Valid @RequestBody CreateUserDto userDto) {
        return instructorsService.updateInstructor(id, userDto);
    }

    @PutMapping("/admins/{id}")
    public Admin updateAdmin(@PathVariable String id, @Valid @RequestBody CreateUserDto userDto) {
        return adminsService.updateAdmin(id, userDto);
    }

    @DeleteMapping("/students/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStudent(@PathVariable String id) {
        studentsService.deleteStudent(id);
    }

    @DeleteMapping("/instructors/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInstructor(@PathVariable String id) {
        instructorsService.deleteInstructor(id);
    }

    @DeleteMapping("/admins/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAdmin(@PathVariable String id, @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        if (id.equals(user.getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot delete yourself.");
        }
        adminsService.deleteAdmin(id);
    }
}
