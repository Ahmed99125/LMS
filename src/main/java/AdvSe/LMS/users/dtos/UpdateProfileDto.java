package AdvSe.LMS.users.dtos;

import AdvSe.LMS.cloudinary.CloudinaryFile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileDto {
    private String email;
    private String password;
    private String phone;
    private CloudinaryFile profilePicture;
}