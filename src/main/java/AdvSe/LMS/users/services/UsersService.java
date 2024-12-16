package AdvSe.LMS.users.services;

import AdvSe.LMS.users.dtos.CreateUserDto;
import AdvSe.LMS.users.entities.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UsersService {


    public UsersService() {
    }

    public void setUserData(User user, CreateUserDto userDto) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(userDto.getPassword());
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setPassword(hashedPassword);
        user.setRole(userDto.getRole());
    }

    public void updateUser(User user, CreateUserDto userDto) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(userDto.getPassword());
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setPassword(hashedPassword);
    }
}
