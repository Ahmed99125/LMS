package AdvSe.LMS.users.controllers;

import AdvSe.LMS.Security.SecurityConfig;
import AdvSe.LMS.users.dtos.CreateUserDto;
import AdvSe.LMS.users.dtos.LoginDto;
import AdvSe.LMS.users.dtos.UpdateProfileDto;
import AdvSe.LMS.users.entities.Admin;
import AdvSe.LMS.users.entities.Instructor;
import AdvSe.LMS.users.entities.Student;
import AdvSe.LMS.users.entities.User;
import AdvSe.LMS.users.services.AdminsService;
import AdvSe.LMS.users.services.InstructorsService;
import AdvSe.LMS.users.services.StudentsService;
import AdvSe.LMS.utils.enums.Role;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("api/users")
public class UsersController {
    private final StudentsService studentsService;
    private final InstructorsService instructorsService;
    private final AdminsService adminsService;
    private final AuthenticationManager authenticationManager;

    public UsersController(StudentsService studentsService, InstructorsService instructorService, AdminsService adminService,
                           AuthenticationManager authenticationManager) {
        this.studentsService = studentsService;
        this.instructorsService = instructorService;
        this.adminsService = adminService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User register(@Valid @RequestBody CreateUserDto userDto) {
        return switch (userDto.getRole()) {
            case STUDENT -> studentsService.createStudent(userDto);
            case INSTRUCTOR -> instructorsService.createInstructor(userDto);
            default -> adminsService.createAdmin(userDto);
        };
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginDto loginDto, HttpSession session) {
        if (loginDto.getUsername() == null || loginDto.getUsername().isEmpty() || loginDto.getPassword() == null || loginDto.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("All fields must be filled: Username and password.");
        }
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));

            // If authentication is successful, return the authenticated user's details
            SecurityContextHolder.getContext().setAuthentication(authentication);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
            return ResponseEntity.ok("Login successful for user: " + authentication.getName());
        } catch (AuthenticationException e) {
            // Handle authentication failure
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
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

    @GetMapping("/profile")
    public User getUser(HttpSession session) {
        Authentication user = SecurityConfig.getLoggedInUser(session);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No user is currently logged in.");
        }
        Role role = Role.valueOf(user.getAuthorities().iterator().next().getAuthority());

        return switch (role) {
            case STUDENT -> studentsService.getStudentById(user.getName());
            case INSTRUCTOR -> instructorsService.getInstructorById(user.getName());
            default -> adminsService.getAdminById(user.getName());
        };
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        SecurityContext securityContext = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
        if (securityContext != null) {
            securityContext.setAuthentication(null); // Clear the authentication
        }
        session.invalidate(); // Invalidate the session
        return ResponseEntity.ok("Logout successful.");
    }

    @PutMapping("/profile/update_profile")
    public User updateProfile(@RequestBody UpdateProfileDto profileDto, HttpSession session) {
        Authentication user = SecurityConfig.getLoggedInUser(session);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No user is currently logged in.");
        }
        String userId = user.getName();
        Role role = Role.valueOf(user.getAuthorities().iterator().next().getAuthority());

        return switch (role) {
            case STUDENT -> studentsService.updateProfile(userId, profileDto);
            case INSTRUCTOR -> instructorsService.updateProfile(userId, profileDto);
            default -> adminsService.updateProfile(userId, profileDto);
        };
    }

    @PutMapping("/profile/update_picture")
    public User updatePicture(@RequestParam("profilePicture") MultipartFile profilePicture, HttpSession session) {
        Authentication user = SecurityConfig.getLoggedInUser(session);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No user is currently logged in.");
        }
        String userId = user.getName();

        Role role = Role.valueOf(user.getAuthorities().iterator().next().getAuthority());

        return switch (role) {
            case STUDENT -> studentsService.updatePicture(userId, profilePicture);
            case INSTRUCTOR -> instructorsService.updatePicture(userId, profilePicture);
            default -> adminsService.updatePicture(userId, profilePicture);
        };
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
    public void deleteAdmin(@PathVariable String id, HttpSession session) {
        Authentication user = SecurityConfig.getLoggedInUser(session);
        if (user.getPrincipal().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot delete your own account.");
        }
        adminsService.deleteAdmin(id);
    }
}
