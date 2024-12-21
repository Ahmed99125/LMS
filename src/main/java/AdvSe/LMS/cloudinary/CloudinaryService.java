package AdvSe.LMS.cloudinary;

import com.cloudinary.Cloudinary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@Service
public class CloudinaryService {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final String baseFolder = "LMS/";
    private final Cloudinary cloudinary;
    private final CloudinaryRepository cloudinaryRepository;

    CloudinaryService(Cloudinary cloudinary, CloudinaryRepository cloudinaryRepository) {
        this.cloudinary = cloudinary;
        this.cloudinaryRepository = cloudinaryRepository;
    }

    private boolean isVideo(MultipartFile file) {
        return file.getContentType() != null && file.getContentType().startsWith("video");
    }

    public CloudinaryFile uploadFile(MultipartFile file, String folder) {
        Map<String, Object> params = new HashMap<>();
        params.put("folder", baseFolder + folder);
        if (isVideo(file)) {
            params.put("resource_type", "video");
        } else if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(BAD_REQUEST, "File size exceeds the limit");
        }
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
            log.info("Uploaded file: {}", uploadResult);
            CloudinaryFile cloudinaryFile = new CloudinaryFile(uploadResult.get("public_id").toString(), uploadResult.get("url").toString(), uploadResult.get("resource_type").toString());
            return cloudinaryRepository.save(cloudinaryFile);
        } catch (Exception e) {
            throw new ResponseStatusException(BAD_REQUEST, "Failed to upload file");
        }
    }

    public void deleteFile(String publicId) {
        if (!cloudinaryRepository.existsById(publicId)) {
            throw new ResponseStatusException(BAD_REQUEST, "File not found");
        }
        cloudinaryRepository.deleteById(publicId);
    }
}
