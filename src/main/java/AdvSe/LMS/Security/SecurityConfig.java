package AdvSe.LMS.Security;

import AdvSe.LMS.users.entities.Admin;
import AdvSe.LMS.users.entities.Instructor;
import AdvSe.LMS.users.entities.Student;
import AdvSe.LMS.users.repositories.AdminsRepository;
import AdvSe.LMS.users.repositories.InstructorsRepository;
import AdvSe.LMS.users.repositories.StudentsRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SecurityConfig {

    private final AdminsRepository adminsRepository;
    private final InstructorsRepository instructorsRepository;
    private final StudentsRepository studentsRepository;

    public SecurityConfig(AdminsRepository adminsRepository,
                          InstructorsRepository instructorsRepository,
                          StudentsRepository studentsRepository) {
        this.adminsRepository = adminsRepository;
        this.instructorsRepository = instructorsRepository;
        this.studentsRepository = studentsRepository;
    }

    public static Authentication getLoggedInUser(HttpSession session) {
        SecurityContext securityContext = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
        Authentication auth = securityContext.getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
            return auth;
        }
        return null;

    }

    @Bean
    public AuthenticationProvider customAuthenticationProvider() {
        return new AuthenticationProvider() {
            @Override

            // Should add the bcrypt compare but at the end so we can login easily while testing.

            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                String username = authentication.getName();
                String password = authentication.getCredentials().toString();

                PasswordEncoder encoder = new BCryptPasswordEncoder();

                // Check Admins table
                Admin admin = adminsRepository.findById(username).orElse(null);
                if (admin != null && password.equals(admin.getPassword())) {
                    List<GrantedAuthority> authorities = new ArrayList<>();
                    authorities.add(new SimpleGrantedAuthority("ADMIN"));
                    return new UsernamePasswordAuthenticationToken(username, password, authorities);
                }

                // Check Instructors table
                Instructor instructor = instructorsRepository.findById(username).orElse(null);
                if (instructor != null && password.equals(instructor.getPassword())) {
                    List<GrantedAuthority> authorities = new ArrayList<>();
                    authorities.add(new SimpleGrantedAuthority("INSTRUCTOR"));
                    return new UsernamePasswordAuthenticationToken(username, password, authorities);
                }

                // Check Students table
                Student student = studentsRepository.findById(username).orElse(null);
                if (student != null && password.equals(student.getPassword())) {
                    List<GrantedAuthority> authorities = new ArrayList<>();
                    authorities.add(new SimpleGrantedAuthority("STUDENT"));
                    return new UsernamePasswordAuthenticationToken(username, password, authorities);
                }

                // If no match is found, throw exception
                throw new AuthenticationException("Invalid username or password") {
                };
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
            }
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(customAuthenticationProvider())
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/login").permitAll() // Allow login only if unauthenticated
                        .requestMatchers("/api/users/user", "/api/users/logout").authenticated() // Require authentication for /user and /logout
                        .requestMatchers("/api/users/students", "/api/users/instructors", "/api/users/admins", "/api/users/register", "/api/users/{id}/**").hasAuthority("ADMIN") // Only admins can access these routes
                        .anyRequest().authenticated() // All other requests require authentication
                )
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // Allow sessions to be created
                .and()
                .httpBasic(); // Use HTTP Basic Authentication
        return http.build();
    }
}
