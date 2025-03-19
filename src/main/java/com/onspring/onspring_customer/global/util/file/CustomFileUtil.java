package com.onspring.onspring_customer.global.util.file;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Component
@Log4j2
@RequiredArgsConstructor
public class CustomFileUtil {
    @Value("${com.onspring-customer.upload.path}")
    private String uploadPath;

    @PostConstruct
    public void init(){
        File tempFolder = new File(uploadPath);

        //폴더가 없으면 만들어줌
        if(!tempFolder.exists()) {
            tempFolder.mkdir();
        }
        //저장할 폴더의 절대 경로를 받아줌
        uploadPath = tempFolder.getAbsolutePath();
        log.info("=============upload Path============");
        log.info(uploadPath);
        log.info("====================================");
    }

    //파일 저장
    public List<String> saveFiles(List<MultipartFile> files) throws RuntimeException {
        if(files == null || files.isEmpty()) {
            return null;
        }

        List<String> uploadNames = new ArrayList<>();

        for(MultipartFile multipartFile : files) {
            String savedName = UUID.randomUUID().toString() + "_" + multipartFile.getOriginalFilename();
            Path savePath = Paths.get(uploadPath, savedName);

            try{
                Files.copy(multipartFile.getInputStream(), savePath);
                //이미지 파일 썸네일 생성
                String contentType = multipartFile.getContentType();

                if(contentType != null && contentType.startsWith("image")) {
                    Path thumbnailPath = Paths.get(uploadPath, "s_" + savedName);

                    Thumbnails.of(savePath.toFile())
                            .size(400,400)
                            .toFile(thumbnailPath.toFile());
                }
                uploadNames.add(savedName);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }

        }//for loop end

        return uploadNames;
    }

    //파일 가져오기
    public ResponseEntity<Resource> getFile(String fileName) {
        Resource resource = new FileSystemResource(uploadPath+ File.separator + fileName);
        if(!resource.exists()) {
            return ResponseEntity.noContent().build();
            //resource = new FileSystemResource(uploadPath+ File.separator + "default.jpg");
        }
        HttpHeaders headers = new HttpHeaders();

        try{
            headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
        } catch(Exception e){
            ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().headers(headers).body(resource);
    }

    //파일 삭제
    public void deleteFiles(List<String> fileNames) {
        if(fileNames == null || fileNames.size() == 0){
            return;
        }
        fileNames.forEach(fileName -> {
            //썸네일 존재 여부 확인
            String thumbnailFileName = "s_" + fileName;
            Path thumbnailPath = Paths.get(uploadPath, thumbnailFileName);
            Path filePath = Paths.get(uploadPath, fileName);

            try{
                Files.deleteIfExists(thumbnailPath);
                log.info(thumbnailPath);
                Files.deleteIfExists(filePath);
            }catch(IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

}
