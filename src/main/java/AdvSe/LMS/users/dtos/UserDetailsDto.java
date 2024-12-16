package AdvSe.LMS.users.dtos;

import AdvSe.LMS.cloudinary.CloudinaryFile;
import AdvSe.LMS.utils.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDto {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private CloudinaryFile profilePictureUrl;
}