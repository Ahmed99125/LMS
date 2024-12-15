package AdvSe.LMS.users.entities;

import AdvSe.LMS.cloudinary.CloudinaryFile;
import AdvSe.LMS.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public class User {
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String email;

    private String phone;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "profile_picture_id")
    private CloudinaryFile profilePicture;

    public User(String id, String name, String password, Role role, String email, String phone, CloudinaryFile profilePicture) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.role = role;
        this.email = email;
        this.phone = phone;
        this.profilePicture = profilePicture;
    }
}
