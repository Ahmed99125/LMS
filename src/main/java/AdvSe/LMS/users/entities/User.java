package AdvSe.LMS.users.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class User {
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    private String email;

    private String phone;

    private String profilePicture;

    public User() {
    }

    public User(String id, String name, String password, String role, String email, String phone, String profilePicture) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.role = role;
        this.email = email;
        this.phone = phone;
        this.profilePicture = profilePicture;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
