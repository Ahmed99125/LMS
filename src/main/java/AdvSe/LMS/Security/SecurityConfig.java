package AdvSe.LMS.Security;

import AdvSe.LMS.users.entities.User;
import AdvSe.LMS.users.repositories.AdminsRepository;
import AdvSe.LMS.users.repositories.InstructorsRepository;
import AdvSe.LMS.users.repositories.StudentsRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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
@EnableMethodSecurity
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

                User user;

                // Check Admins table
                user = adminsRepository.findById(username).orElse(null);
                if (user == null) {
                    user = instructorsRepository.findById(username).orElse(null);
                }
                if (user == null) {
                    user = studentsRepository.findById(username).orElse(null);
                }
                if (user != null && encoder.matches(password, user.getPassword())) {
                    List<GrantedAuthority> authorities = new ArrayList<>();
                    authorities.add(new SimpleGrantedAuthority(user.getRole().name()));
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
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/login").permitAll() // Allow login only if unauthenticated
                        .requestMatchers("/api/users/students/**", "/api/users/instructors/**", "/api/users/admins/**", "/api/users/register").hasAuthority("ADMIN") // Only admins can access these routes
                        .anyRequest().authenticated() // All other requests require authentication
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .httpBasic(Customizer.withDefaults()); // Use HTTP Basic Authentication
        return http.build();
    }
}
