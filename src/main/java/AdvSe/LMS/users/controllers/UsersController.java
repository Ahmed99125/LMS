package AdvSe.LMS.users.controllers;

import AdvSe.LMS.Security.SecurityConfig;
import AdvSe.LMS.users.dtos.CreateUserDto;
import AdvSe.LMS.users.dtos.LoginDto;
import AdvSe.LMS.users.dtos.UpdateProfileDto;
import AdvSe.LMS.users.dtos.UserDetailsDto;
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
import AdvSe.LMS.cloudinary.CloudinaryService;
import org.springframework.web.multipart.MultipartFile;
import AdvSe.LMS.cloudinary.CloudinaryFile;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("api/users")
public class UsersController {
    private final StudentsService studentsService;
    private final InstructorsService instructorsService;
    private final AdminsService adminsService;
    private final AuthenticationManager authenticationManager;
    private final CloudinaryService cloudinaryService;

    public UsersController(StudentsService studentsService, InstructorsService instructorService, AdminsService adminService, 
            AuthenticationManager authenticationManager, CloudinaryService cloudinaryService) {
		this.studentsService = studentsService;
		this.instructorsService = instructorService;
		this.adminsService = adminService;
		this.authenticationManager = authenticationManager;
		this.cloudinaryService = cloudinaryService; // Inject CloudinaryService
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
            String role = authentication.getAuthorities().iterator().next().getAuthority(); // Get the role
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

    @GetMapping("/user")
    public User getUser(HttpSession session) {
        Authentication user = SecurityConfig.getLoggedInUser(session);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No user is currently logged in.");
        }
        return switch (user.getAuthorities().iterator().next().getAuthority()) {
            case "STUDENT" -> studentsService.getStudentById(user.getName());
            case "INSTRUCTOR" -> instructorsService.getInstructorById(user.getName());
            default -> adminsService.getAdminById(user.getName());
        };
    }
    
    @GetMapping("/mydetails")
    public ResponseEntity<UserDetailsDto> getMyDetails() {
        // Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName(); // Assuming the username is the user ID
        String role = authentication.getAuthorities().iterator().next().getAuthority(); // Get the user's role

        UserDetailsDto userDetails;

        // Retrieve user details based on the role
        switch (role) {
            case "STUDENT":
                Student student = studentsService.getStudentById(userId);
                userDetails = new UserDetailsDto(student.getId(), student.getName(), student.getEmail(), student.getPhone(), "STUDENT", student.getProfilePicture());
                break;
            case "INSTRUCTOR":
                Instructor instructor = instructorsService.getInstructorById(userId);
                userDetails = new UserDetailsDto(instructor.getId(), instructor.getName(), instructor.getEmail(), instructor.getPhone(), "INSTRUCTOR", instructor.getProfilePicture());
                break;
            case "ADMIN":
                Admin admin = adminsService.getAdminById(userId);
                userDetails = new UserDetailsDto(admin.getId(), admin.getName(), admin.getEmail(), admin.getPhone(), "ADMIN", admin.getProfilePicture());
                break;
            default:
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // Handle case if the role is unexpected
        }

        return ResponseEntity.ok(userDetails);
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

    @PostMapping("/myprofile/update")
    public ResponseEntity<String> updateMyProfile(
            @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "phone", required = false) String phone,
            HttpSession session) {

        UpdateProfileDto profileDto = new UpdateProfileDto(email, password, phone, null); // Initialize with other fields

        // Process the profile picture if it exists
        if (profilePicture != null && !profilePicture.isEmpty()) {
            try {
            	CloudinaryFile uploaded = cloudinaryService.uploadFile(profilePicture, "profilePictures");
                profileDto.setProfilePicture(uploaded); 
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload profile picture.");
            }
        }

        // Call the relevant service to update the profile
        Authentication user = (Authentication) session.getAttribute("SPRING_SECURITY_CONTEXT");
        String userId = user.getName();
        String role = user.getAuthorities().iterator().next().getAuthority();
        Role userRole = Role.valueOf(role);

        switch (userRole) {
            case STUDENT -> studentsService.updateProfile(userId, profileDto);
            case INSTRUCTOR -> instructorsService.updateProfile(userId, profileDto);
            case ADMIN -> adminsService.updateProfile(userId, profileDto);
            default -> {
                return ResponseEntity.badRequest().body("Invalid user role.");
            }
        }

        return ResponseEntity.ok("User was updated successfully.");
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
