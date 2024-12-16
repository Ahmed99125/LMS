package AdvSe.LMS.users.controllers;

import AdvSe.LMS.Security.SecurityConfig;
import AdvSe.LMS.users.dtos.CreateUserDto;
import AdvSe.LMS.users.dtos.LoginDto;
import AdvSe.LMS.users.entities.Admin;
import AdvSe.LMS.users.entities.Instructor;
import AdvSe.LMS.users.entities.Student;
import AdvSe.LMS.users.entities.User;
import AdvSe.LMS.users.services.AdminsService;
import AdvSe.LMS.users.services.InstructorsService;
import AdvSe.LMS.users.services.StudentsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@RestController
@RequestMapping("api/users")
public class UsersController {
    private final StudentsService studentsService;
    private final InstructorsService instructorService;
    private final AdminsService adminService;
    private final AuthenticationManager authenticationManager;

    public UsersController(StudentsService studentsService, InstructorsService instructorService, AdminsService adminService, AuthenticationManager authenticationManager) {
        this.studentsService = studentsService;
        this.instructorService = instructorService;
        this.adminService = adminService;
        this.authenticationManager = authenticationManager;
    }


    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User register(@RequestBody CreateUserDto userDto) {
        return switch (userDto.getRole()) {
            case STUDENT -> studentsService.createStudent(userDto);
            case INSTRUCTOR -> instructorService.createInstructor(userDto);
            default -> adminService.createAdmin(userDto);
        };
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto, HttpSession session) {
        if (loginDto.getUsername() == null || loginDto.getUsername().isEmpty() || loginDto.getPassword() == null || loginDto.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("All fields must be filled: Username and password.");
        }
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));

            // If authentication is successful, return the authenticated user's details
            SecurityContextHolder.getContext().setAuthentication(authentication);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
            String role = authentication.getAuthorities().iterator().next().getAuthority(); // Get the role
            return ResponseEntity.ok("Login successful for user: " + authentication.getName());
        } catch (AuthenticationException e) {
            // Handle authentication failure
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }


    // Other routes (unchanged)
    @GetMapping("/students")
    public List<Student> getStudents() {
        return studentsService.getStudents();
    }

    @GetMapping("/instructors")
    public List<Instructor> getInstructors() {
        return instructorService.getInstructors();
    }

    @GetMapping("/admins")
    public List<Admin> getAdmins() {
        return adminService.getAdmins();
    }

    @GetMapping("/user")
    public User getUser(HttpSession session) {
        Authentication user = SecurityConfig.getLoggedInUser(session);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No user is currently logged in.");
        }
        return switch (user.getAuthorities().iterator().next().getAuthority()) {
            case "STUDENT" -> studentsService.getStudentById(user.getName());
            case "INSTRUCTOR" -> instructorService.getInstructorById(user.getName());
            default -> adminService.getAdminById(user.getName());
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

    @PutMapping("/students/{id}")
    public Student updateStudent(@PathVariable String id, @RequestBody CreateUserDto userDto) {
        return studentsService.updateStudent(id, userDto);
    }

    @PutMapping("/instructors/{id}")
    public Instructor updateInstructor(@PathVariable String id, @RequestBody CreateUserDto userDto) {
        return instructorService.updateInstructor(id, userDto);
    }

    @PutMapping("/admins/{id}")
    public Admin updateAdmin(@PathVariable String id, @RequestBody CreateUserDto userDto) {
        return adminService.updateAdmin(id, userDto);
    }

    @DeleteMapping("/students/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStudent(@PathVariable String id) {
        studentsService.deleteStudent(id);
    }

    @DeleteMapping("/instructors/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInstructor(@PathVariable String id) {
        instructorService.deleteInstructor(id);
    }

    @DeleteMapping("/admins/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAdmin(@PathVariable String id, HttpSession session) {
        Authentication user = SecurityConfig.getLoggedInUser(session);
        if (user.getPrincipal().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot delete your own account.");
        }
        adminService.deleteAdmin(id);
    }

}
