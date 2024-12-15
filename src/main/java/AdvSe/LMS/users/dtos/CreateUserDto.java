package AdvSe.LMS.users.dtos;

import AdvSe.LMS.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDto {
    private String id;
    private String name;
    private String password;
    private Role role;
    private MultipartFile image;
}
