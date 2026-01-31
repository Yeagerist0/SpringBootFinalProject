package com.expensetracker.util;

import com.expensetracker.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileUploadUtil {
    
    @Value("${app.aws.s3.bucket-name}")
    private String bucketName;
    
    @Value("${app.aws.s3.region}")
    private String region;
    
    @Value("${app.aws.s3.access-key}")
    private String accessKey;
    
    @Value("${app.aws.s3.secret-key}")
    private String secretKey;
    
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "pdf");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    
    private S3Client getS3Client() {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);
        
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }
    
    public String uploadFile(MultipartFile file, String folder) {
        validateFile(file);
        
        String fileName = generateFileName(file.getOriginalFilename());
        String key = folder + "/" + fileName;
        
        try {
            S3Client s3Client = getS3Client();
            
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();
            
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            
            String fileUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key);
            log.info("File uploaded successfully: {}", fileUrl);
            
            return fileUrl;
        } catch (IOException e) {
            log.error("Error uploading file to S3", e);
            throw new BadRequestException("Failed to upload file");
        }
    }
    
    public void deleteFile(String fileUrl) {
        try {
            String key = extractKeyFromUrl(fileUrl);
            
            S3Client s3Client = getS3Client();
            
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            
            s3Client.deleteObject(deleteObjectRequest);
            log.info("File deleted successfully: {}", fileUrl);
        } catch (Exception e) {
            log.error("Error deleting file from S3", e);
            // Don't throw exception, just log the error
        }
    }
    
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File size exceeds maximum allowed size of 10MB");
        }
        
        String extension = getFileExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BadRequestException("File type not allowed. Allowed types: " + String.join(", ", ALLOWED_EXTENSIONS));
        }
    }
    
    private String generateFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        return UUID.randomUUID().toString() + "." + extension;
    }
    
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            throw new BadRequestException("Invalid file name");
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
    
    private String extractKeyFromUrl(String fileUrl) {
        // Extract key from URL: https://bucket.s3.region.amazonaws.com/key
        String[] parts = fileUrl.split(".amazonaws.com/");
        if (parts.length < 2) {
            throw new BadRequestException("Invalid file URL");
        }
        return parts[1];
    }
}
