package AdvSe.LMS.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;
    private final CloudinaryRepository cloudinaryRepository;
    private final String baseFolder = "LMS/";

    CloudinaryService(Cloudinary cloudinary, CloudinaryRepository cloudinaryRepository) {
        this.cloudinary = cloudinary;
        this.cloudinaryRepository = cloudinaryRepository;
    }

    public CloudinaryFile uploadFile(MultipartFile file, String folder) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("folder", baseFolder + folder));
        CloudinaryFile cloudinaryFile = new CloudinaryFile(uploadResult.get("public_id").toString(), uploadResult.get("url").toString(), uploadResult.get("type").toString());
        return cloudinaryRepository.save(cloudinaryFile);
    }

    public List<CloudinaryFile> uploadMultipleFiles(List<MultipartFile> files, String folder) throws IOException {
        List<CloudinaryFile> result = new ArrayList<>();
        for (MultipartFile file : files) {
            result.add(uploadFile(file, folder));
        }
        return result;
    }

    public String deleteFile(String publicId) throws IOException {
        Map deleteResult = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        cloudinaryRepository.deleteById(publicId);
        return deleteResult.get("result").toString();
    }
}
