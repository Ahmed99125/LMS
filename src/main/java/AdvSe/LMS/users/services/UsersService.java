package AdvSe.LMS.users.services;

import AdvSe.LMS.cloudinary.CloudinaryFile;
import AdvSe.LMS.cloudinary.CloudinaryService;
import AdvSe.LMS.users.dtos.CreateUserDto;
import AdvSe.LMS.users.dtos.UpdateProfileDto;
import AdvSe.LMS.users.entities.User;
import AdvSe.LMS.users.repositories.UsersRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;


@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final CloudinaryService cloudinaryService;

    public UsersService(UsersRepository usersRepository, CloudinaryService cloudinaryService) {
        this.usersRepository = usersRepository;
        this.cloudinaryService = cloudinaryService;
    }

    public void setUserData(User user, CreateUserDto userDto) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(userDto.getPassword());
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setPassword(hashedPassword);
    }

    public void updateUser(User user, CreateUserDto userDto) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(userDto.getPassword());
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setPassword(hashedPassword);
    }

    public void updateProfile(User user, UpdateProfileDto profileDto) {
        if (profileDto.getEmail() != null) user.setName(profileDto.getEmail());

        if (profileDto.getPassword() != null) {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            user.setPassword(passwordEncoder.encode(profileDto.getPassword()));
        }

        if (profileDto.getPhone() != null) user.setPhone(profileDto.getPhone());
    }

    public void updatePicture(User user, MultipartFile profilePicture) {
        if (!profilePicture.getContentType().startsWith("image")) {
            throw new IllegalArgumentException("Only images are allowed");
        }
        CloudinaryFile cloudinaryFile = cloudinaryService.uploadFile(profilePicture, "profile_pictures");
        user.setProfilePicture(cloudinaryFile);
    }

    public User getLoggedInUser(org.springframework.security.core.userdetails.User user) {
        if (user == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "No user is currently logged in.");
        }
        return usersRepository.findById(user.getUsername()).orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "User not found"));
    }
}
