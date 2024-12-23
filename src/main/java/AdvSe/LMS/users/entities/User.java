package AdvSe.LMS.users.entities;

import AdvSe.LMS.cloudinary.CloudinaryFile;
import AdvSe.LMS.notifications.Notification;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)
@Table(name = "users")
public class User {
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phone;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "profile_picture_id")
    private CloudinaryFile profilePicture;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Notification> notifications = new ArrayList<>();

    public User(String id, String name, String password, String email, String phone, CloudinaryFile profilePicture) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.profilePicture = profilePicture;
    }

    public void addNotification(Notification notification) {
        notifications.add(notification);
    }

    public void removeNotification(Notification notification) {
        notifications.remove(notification);
    }

    @JsonIgnore
    public String getRole() {
        return this.getClass().getAnnotation(DiscriminatorValue.class).value();
    }
}
