package AdvSe.LMS.cloudinary;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cloudinary_files")
public class CloudinaryFile {
    @Id
    private String publicId;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String type;
}
