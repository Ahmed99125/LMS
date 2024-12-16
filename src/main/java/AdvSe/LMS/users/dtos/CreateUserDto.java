package AdvSe.LMS.users.dtos;

import AdvSe.LMS.utils.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDto {
    @NotBlank
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private String password;

    @NotNull
    private Role role;
}
