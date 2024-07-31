package com.talktown.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;
    private final Logger logger = LoggerFactory.getLogger(CloudinaryService.class);

    public CloudinaryService(@Value("${CLOUDINARY_URL}") String cloudinaryUrl) {
        this.cloudinary = new Cloudinary(cloudinaryUrl);
        logger.info("Cloudinary initialized with URL: {}", cloudinaryUrl);
    }

    public Map<String, Object> uploadImage(MultipartFile file, String userId) throws IOException {
        String folderPath = "talktown/" + userId;
        logger.info("Attempting to upload image for user {} to folder {}", userId, folderPath);

        try {
            Map<String, Object> params = ObjectUtils.asMap(
                    "folder", folderPath,
                    "resource_type", "auto"
            );
            Map<String, Object> result = this.cloudinary.uploader().upload(file.getBytes(), params);
            logger.info("Image uploaded successfully. Result: {}", result);
            return result;
        } catch (IOException e) {
            logger.error("Failed to upload image for user {}", userId, e);
            throw e;
        }
    }
}
