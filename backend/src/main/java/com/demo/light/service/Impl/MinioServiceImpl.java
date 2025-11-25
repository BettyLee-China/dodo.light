package com.demo.light.service.Impl;

import com.demo.light.service.MinioService;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.UUID;

@Service
public class MinioServiceImpl implements MinioService {
    @Autowired
    private MinioClient minioClient;
    private final String bucketName = "images";

    private static final Logger log = LoggerFactory.getLogger(MinioServiceImpl.class);


    @Override
    public String uploadImage(MultipartFile file) {
        createBucketIfNoExists();

        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传的文件为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IllegalArgumentException("文件名无效");
        }

        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String objectName = UUID.randomUUID() + fileExtension;

        try (InputStream inputStream = file.getInputStream()) {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build();

            minioClient.putObject(putObjectArgs);

            log.info("✅ 文件上传 MinIO 成功: {}", objectName);
            return objectName;
        } catch (IOException e) {
            log.error("文件读取失败", e);
            throw new RuntimeException("文件读取失败", e);
        } catch (ErrorResponseException e) {
            log.error("MinIO 服务端错误: 错误码={}, 错误消息={}", e.errorResponse().message(), e);
            throw new RuntimeException("MinIO 服务端错误: " + e.errorResponse().message(), e);
        } catch (InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | NoSuchAlgorithmException | ServerException |
                 XmlParserException | IllegalArgumentException e) {
            log.error("MinIO 客户端处理失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("未知上传错误", e);
            throw new RuntimeException("文件上传失败（未知错误）", e); // 👈 至少带上 e
        }
    }

    @Override
    public String getPresignedUrl(String objectName, Duration duration) {
        try {
        GetPresignedObjectUrlArgs args= GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucketName)
                .object(objectName)
                .build();
            return minioClient.getPresignedObjectUrl(args);
        } catch (Exception e) {
            log.error("生成预签名URL失败：objectName={}",objectName,e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createBucketIfNoExists() {
        boolean found= false;
        try {
            found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (ErrorResponseException e) {
            throw new RuntimeException(e);
        } catch (InsufficientDataException e) {
            throw new RuntimeException(e);
        } catch (InternalException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (InvalidResponseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (ServerException e) {
            throw new RuntimeException(e);
        } catch (XmlParserException e) {
            throw new RuntimeException(e);
        }
        if(!found){
            try {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            } catch (ErrorResponseException e) {
                throw new RuntimeException(e);
            } catch (InsufficientDataException e) {
                throw new RuntimeException(e);
            } catch (InternalException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            } catch (InvalidResponseException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (ServerException e) {
                throw new RuntimeException(e);
            } catch (XmlParserException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void deleteImage(String objectName){
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            System.out.println("✅ 文件已删除: " + objectName);
        } catch (Exception e) {
            System.err.println("❌ 删除文件失败: " + e.getMessage());
            throw new RuntimeException("删除文件失败: " + objectName, e);
        }
    }


}
