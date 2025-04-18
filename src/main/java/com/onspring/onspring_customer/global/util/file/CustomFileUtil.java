package com.onspring.onspring_customer.global.util.file;

import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomFileUtil {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;  // S3 버킷 이름


    public List<String> saveFiles(List<MultipartFile> files) throws IOException, IOException {

        if (files == null || files.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> uploadedFileNames = new ArrayList<>();

        for (MultipartFile multipartFile : files) {
            String fileName = UUID.randomUUID().toString() + "_" + multipartFile.getOriginalFilename();
            String fileKey = "uploads/" + fileName;

            // 이미지 파일이라면 압축
            if (isImage(fileName)) {
                InputStream inputStream = multipartFile.getInputStream();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                // 이미지 압축 (이미지 크기를 80%로 줄이기)
                Thumbnails.of(inputStream)
                        .size(800, 800) // 크기 제한
                        .outputFormat("jpg") // 형식 변환 (필요에 따라 수정)
                        .toOutputStream(outputStream);

                byte[] compressedBytes = outputStream.toByteArray();

                // S3에 파일 업로드
                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileKey)
                        .contentType("application/octet-stream")
                        .build();

                s3Client.putObject(putObjectRequest, RequestBody.fromBytes(compressedBytes));

                uploadedFileNames.add(fileName);
            } else {
                // 이미지가 아니면 원본 그대로 업로드
                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileKey)
                        .contentType("application/octet-stream")
                        .build();

                s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));
                uploadedFileNames.add(fileName);
            }
        }

        return uploadedFileNames;
    }
//    // 파일 저장 (S3에 업로드)
//    public List<String> saveFiles(List<MultipartFile> files) throws IOException {
//
//        if (files == null || files.isEmpty()) {
//            return Collections.emptyList();
//        }
//
//        List<String> uploadedFileNames = new ArrayList<>();
//
//        for (MultipartFile multipartFile : files) {
//            String fileName = UUID.randomUUID().toString() + "_" + multipartFile.getOriginalFilename();
//            String fileKey = "uploads/" + fileName;
//
//            // S3에 파일 업로드
//            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//                    .bucket(bucketName)
//                    .key(fileKey)
//                    .contentType("application/octet-stream")
//                    .build();
//
//            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));
//
//            uploadedFileNames.add(fileName);
//
//            // 이미지 파일이면 썸네일을 생성하여 S3에 업로드
//            if (isImage(fileName)) {
//                String thumbnailFileKey = "uploads/s_" + fileName;
//                // 썸네일 생성 (썸네일 생성 로직은 구현 필요)
//            }
//        }
//
//        return uploadedFileNames;
//    }

    // 파일 삭제 (S3에서 삭제)
    public void deleteFiles(List<String> fileNames) {
        if (fileNames == null || fileNames.isEmpty()) {
            return;
        }

        for (String fileName : fileNames) {
            String fileKey = "uploads/" + fileName;
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        }
    }

    // 이미지인지 확인하는 유틸리티 메소드 (파일 이름으로 판단)
    private boolean isImage(String fileName) {
        String[] imageExtensions = {"jpg", "jpeg", "png", "gif"};
        for (String extension : imageExtensions) {
            if (fileName.toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }


    public ResponseEntity<Resource> getFile(String fileName) {
        String fileKey = "uploads/" + fileName;
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();

        // S3에서 직접 InputStream 획득 (메모리 적재 X)
        InputStream s3InputStream = s3Client.getObject(getObjectRequest);
        Resource resource = new InputStreamResource(s3InputStream);

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + fileName + "\"");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

//    // 파일 가져오기 (S3에서 다운로드)
//    public ResponseEntity<Resource> getFile(String fileName) {
//        String fileKey = "uploads/" + fileName;
//        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
//                .bucket(bucketName)
//                .key(fileKey)
//                .build();
//
//        byte[] fileBytes = s3Client.getObjectAsBytes(getObjectRequest).asByteArray();
//        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(fileBytes));
//
//        return ResponseEntity.ok().body(resource);
//    }

}
