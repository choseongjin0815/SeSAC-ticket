package com.onspring.onspring_customer.global.util.file;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomFileUtil {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;  // S3 버킷 이름

    // 파일 저장 (S3에 업로드)
    public List<String> saveFiles(List<MultipartFile> files) throws IOException {
        List<String> uploadedFileNames = new ArrayList<>();

        for (MultipartFile multipartFile : files) {
            String fileName = UUID.randomUUID().toString() + "_" + multipartFile.getOriginalFilename();
            String fileKey = "uploads/" + fileName;

            // S3에 파일 업로드
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .contentType("application/octet-stream")
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));

            uploadedFileNames.add(fileName);

            // 이미지 파일이면 썸네일을 생성하여 S3에 업로드
            if (isImage(fileName)) {
                String thumbnailFileKey = "uploads/s_" + fileName;
                // 썸네일 생성 (썸네일 생성 로직은 구현 필요)
            }
        }

        return uploadedFileNames;
    }

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

    // 파일 가져오기 (S3에서 다운로드)
    public ResponseEntity<Resource> getFile(String fileName) {
        String fileKey = "uploads/" + fileName;
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();

        byte[] fileBytes = s3Client.getObjectAsBytes(getObjectRequest).asByteArray();
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(fileBytes));

        return ResponseEntity.ok().body(resource);
    }

}
//package com.onspring.onspring_customer.global.util.file;
//
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import net.coobird.thumbnailator.Thumbnails;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.FileSystemResource;
//import org.springframework.core.io.Resource;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Component;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//
//@Component
//@Log4j2
//@RequiredArgsConstructor
//public class CustomFileUtil {
//    @Value("${com.onspring-customer.upload.path}")
//    private String uploadPath;
//
//    @PostConstruct
//    public void init(){
//        File tempFolder = new File(uploadPath);
//
//        //폴더가 없으면 만들어줌
//        if(!tempFolder.exists()) {
//            tempFolder.mkdir();
//        }
//        //저장할 폴더의 절대 경로를 받아줌
//        uploadPath = tempFolder.getAbsolutePath();
//        log.info("=============upload Path============");
//        log.info(uploadPath);
//        log.info("====================================");
//    }
//
//    //파일 저장
//    public List<String> saveFiles(List<MultipartFile> files) throws RuntimeException {
//        if(files == null || files.isEmpty()) {
//            return null;
//        }
//
//        List<String> uploadNames = new ArrayList<>();
//
//        for(MultipartFile multipartFile : files) {
//            String savedName = UUID.randomUUID().toString() + "_" + multipartFile.getOriginalFilename();
//            Path savePath = Paths.get(uploadPath, savedName);
//
//            try{
//                Files.copy(multipartFile.getInputStream(), savePath);
//                //이미지 파일 썸네일 생성
//                String contentType = multipartFile.getContentType();
//
//                if(contentType != null && contentType.startsWith("image")) {
//                    Path thumbnailPath = Paths.get(uploadPath, "s_" + savedName);
//
//                    Thumbnails.of(savePath.toFile())
//                            .size(400,400)
//                            .toFile(thumbnailPath.toFile());
//                }
//                uploadNames.add(savedName);
//            } catch (IOException e) {
//                throw new RuntimeException(e.getMessage());
//            }
//
//        }//for loop end
//
//        return uploadNames;
//    }
//
//    //파일 가져오기
//    public ResponseEntity<Resource> getFile(String fileName) {
//        Resource resource = new FileSystemResource(uploadPath+ File.separator + fileName);
//        if(!resource.exists()) {
//            return ResponseEntity.noContent().build();
//            //resource = new FileSystemResource(uploadPath+ File.separator + "default.jpg");
//        }
//        HttpHeaders headers = new HttpHeaders();
//
//        try{
//            headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
//        } catch(Exception e){
//            ResponseEntity.internalServerError().build();
//        }
//        return ResponseEntity.ok().headers(headers).body(resource);
//    }
//
//    //파일 삭제
//    public void deleteFiles(List<String> fileNames) {
//        if(fileNames == null || fileNames.size() == 0){
//            return;
//        }
//        fileNames.forEach(fileName -> {
//            //썸네일 존재 여부 확인
//            String thumbnailFileName = "s_" + fileName;
//            Path thumbnailPath = Paths.get(uploadPath, thumbnailFileName);
//            Path filePath = Paths.get(uploadPath, fileName);
//
//            try{
//                Files.deleteIfExists(thumbnailPath);
//                log.info(thumbnailPath);
//                Files.deleteIfExists(filePath);
//            }catch(IOException e) {
//                throw new RuntimeException(e.getMessage());
//            }
//        });
//    }
//
//}
