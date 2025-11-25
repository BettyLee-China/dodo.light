package com.demo.light.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;

@Service
public interface MinioService {
    String uploadImage(MultipartFile file);

    String getPresignedUrl(String objectName, Duration duration);

    void createBucketIfNoExists();

    void deleteImage(String objectName);
}
