package AdvSe.LMS.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.persistence.PreRemove;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class CloudinaryListener {
    private final Cloudinary cloudinary;

    public CloudinaryListener(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @PreRemove
    private void deleteFromCloudinary(CloudinaryFile cloudinaryFile) {
        try {
            Map deleteResult = cloudinary.uploader().destroy(cloudinaryFile.getPublicId(), ObjectUtils.emptyMap());
            log.info("Deleted file: {}", deleteResult.get("result"));
        } catch (Exception e) {
            log.info("Failed to delete file: {}", e.toString());
        }
    }
}
