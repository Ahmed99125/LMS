package AdvSe.LMS.users.controllers;

import AdvSe.LMS.Security.SecurityConfig;
import AdvSe.LMS.users.dtos.CreateUserDto;
import AdvSe.LMS.users.dtos.LoginDto;
import AdvSe.LMS.users.entities.Admin;
import AdvSe.LMS.users.entities.Instructor;
import AdvSe.LMS.users.entities.Student;
import AdvSe.LMS.users.repositories.AdminsRepository;
import AdvSe.LMS.users.repositories.InstructorsRepository;
import AdvSe.LMS.users.repositories.StudentsRepository;
import AdvSe.LMS.users.services.UserService;
import AdvSe.LMS.utils.enums.Role;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import java.util.List;


@RestController
@RequestMapping("api/users")
public class UsersController {
    private final UserService userService;
    private final StudentsRepository studentsRepository;
    private final InstructorsRepository instructorsRepository;
    private final AdminsRepository adminsRepository;
    private final AuthenticationManager authenticationManager;

    public UsersController(UserService userService, 
                           StudentsRepository studentsRepository,
                           InstructorsRepository instructorsRepository,
                           AdminsRepository adminsRepository,
                           AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.studentsRepository = studentsRepository;
        this.instructorsRepository = instructorsRepository;
        this.adminsRepository = adminsRepository;
        this.authenticationManager = authenticationManager;
    }
    
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody CreateUserDto userDto) {
        // Validate that all fields are filled
        if (userDto.getName() == null || userDto.getName().isEmpty() ||
            userDto.getPassword() == null || userDto.getPassword().isEmpty() ||
            userDto.getId() == null || userDto.getId().isEmpty() ||
            userDto.getRole() == null) {
            return ResponseEntity.badRequest().body("All fields must be filled: Id, username, password, and role.");
        }        

        if (adminsRepository.existsById(userDto.getId())) {
            return ResponseEntity.badRequest().body("User with ID " + userDto.getId() + " already exists.");
        }
        if (instructorsRepository.existsById(userDto.getId())) {
            return ResponseEntity.badRequest().body("User with ID " + userDto.getId() + " already exists.");
        }
        if (studentsRepository.existsById(userDto.getId())) {
            return ResponseEntity.badRequest().body("User with ID " + userDto.getId() + " already exists.");
        }

        try {
            Role role = userDto.getRole();
            if (role == Role.ADMIN) {
                userService.createAdmin(userDto);
            } else if (role == Role.STUDENT) {
                userService.createStudent(userDto);
            } else if (role == Role.INSTRUCTOR) {
                userService.createInstructor(userDto);
            } else {
                return ResponseEntity.badRequest().body("Role must be one of the following: STUDENT, INSTRUCTOR, ADMIN.");
            }
            return ResponseEntity.ok("User registered successfully.");
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred during registration: " + e.getMessage());
        }
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto, HttpSession session) {
    	if(loginDto.getUsername() == null || loginDto.getUsername().isEmpty() || loginDto.getPassword() == null || loginDto.getPassword().isEmpty()) {
    		return ResponseEntity.badRequest().body("All fields must be filled: Username and password.");
    	}
        try {        	
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
            );

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
        return studentsRepository.findAll();
    }
    
    @GetMapping("/instructors")
    public List<Instructor> getInstructors() {
        return instructorsRepository.findAll();
    }
    
    @GetMapping("/admins")
    public List<Admin> getAdmins() {
        return adminsRepository.findAll();
    }

    @GetMapping("/user")
    public ResponseEntity<String> getUser(HttpSession session) {
    	Authentication user = SecurityConfig.getLoggedInUser(session);
        if (user != null) {
            return ResponseEntity.ok("Logged-in user: " + user.getPrincipal());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No user is currently logged in.");
        }
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
    
    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable String id, @RequestBody CreateUserDto userDto, HttpSession session) {
        // Check if the logged-in user is an admin
        Authentication user = SecurityConfig.getLoggedInUser(session);
        if(user.getPrincipal().equals(id)) {
        	return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can't update Yourself.");
        }
        
        if (userDto.getName() == null || userDto.getName().isEmpty() ||
            userDto.getPassword() == null || userDto.getPassword().isEmpty() ||
            userDto.getId() == null || userDto.getId().isEmpty() ||
            userDto.getRole() == null) {
            return ResponseEntity.badRequest().body("All fields must be filled: Id, Name, password, and role.");
        }

        // Update user logic
        try {
            String res = userService.updateUser(id, userDto);
            if(res.equals("not found")) return ResponseEntity.ok("User can't be found.");
            else if(res.equals("Invalid data")) return ResponseEntity.ok("You entered Invalid data.");
            return ResponseEntity.ok("User updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update user: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id, HttpSession session) {
    	Authentication user = SecurityConfig.getLoggedInUser(session);
    	if(user.getPrincipal().equals(id)) {
        	return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can't update Yourself.");
        }

        try {
            String res = userService.deleteUser(id);
            if(res.equals("not found")) return ResponseEntity.ok("User can't be found.");
            return ResponseEntity.ok("User deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to delete user: " + e.getMessage());
        }
    }
    
}
