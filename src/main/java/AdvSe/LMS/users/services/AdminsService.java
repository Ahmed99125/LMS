package AdvSe.LMS.users.services;

import AdvSe.LMS.users.dtos.CreateUserDto;
import AdvSe.LMS.users.dtos.UpdateProfileDto;
import AdvSe.LMS.users.entities.Admin;
import AdvSe.LMS.users.repositories.AdminsRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class AdminsService {

    private final UsersService usersService;
    private final AdminsRepository adminsRepository;

    public AdminsService(UsersService usersService, AdminsRepository adminsRepository) {
        this.usersService = usersService;
        this.adminsRepository = adminsRepository;
    }

    public List<Admin> getAdmins() {
        return adminsRepository.findAll();
    }

    public Admin createAdmin(CreateUserDto userDto) {
        Admin admin = new Admin();
        usersService.setUserData(admin, userDto);
        return adminsRepository.save(admin);
    }

    public Admin updateAdmin(String adminId, CreateUserDto userDto) {
        Admin admin = adminsRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Admin not found"));
        usersService.updateUser(admin, userDto);
        return adminsRepository.save(admin);
    }

    public Admin getAdminById(String adminId) {
        return adminsRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Admin not found"));
    }

    public void deleteAdmin(String adminId) {
        if (!adminsRepository.existsById(adminId))
            throw new ResponseStatusException(NOT_FOUND, "Admin not found");
        adminsRepository.deleteById(adminId);
    }

    public Admin updateProfile(String id, UpdateProfileDto profileDto) {
        Admin user = adminsRepository.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Admin not found"));

        usersService.updateProfile(user, profileDto);

        return adminsRepository.save(user);
    }

    public Admin updatePicture(String id, MultipartFile profilePicture) {
        Admin user = adminsRepository.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Admin not found"));

        usersService.updatePicture(user, profilePicture);

        return adminsRepository.save(user);
    }
}
