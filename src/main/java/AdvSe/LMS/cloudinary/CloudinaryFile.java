package AdvSe.LMS.cloudinary;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cloudinary_files")
public class CloudinaryFile {
    @Id
    private String publicId;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String type;

    public CloudinaryFile() {
    }

    public CloudinaryFile(String publicId, String url, String type) {
        this.publicId = publicId;
        this.url = url;
        this.type = type;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
