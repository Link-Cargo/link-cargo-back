package com.example.linkcargo.domain.user;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.linkcargo.global.response.code.resultCode.ErrorStatus;
import com.example.linkcargo.global.response.exception.handler.GeneralHandler;
import com.example.linkcargo.global.response.exception.handler.UsersHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserS3Service {

    private final AmazonS3 amazonS3;
    private final UserRepository userRepository;

    @Value("${cloud.aws.s3.profile-bucket}")
    private String bucketName;

    private String getImageKey(Long userId, String fileExtension) {
        return userId + "." + fileExtension;
    }

    @Transactional
    public void deleteExistingImage(Long userId) {
        // 유저 ID를 파일명으로 갖는 모든 확장자의 파일을 삭제
        String[] extensions = {"jpg", "jpeg", "png"};  // 삭제할 확장자 목록
        for (String extension : extensions) {
            String key = getImageKey(userId, extension);
            if (amazonS3.doesObjectExist(bucketName, key)) {
                amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
            }
        }

        // 유저 엔티티에서도 프로필 이미지 초기화
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UsersHandler(ErrorStatus.USER_NOT_FOUND));
        user.resetProfile();
    }

    @Transactional
    public void uploadProfileImage(Long userId, MultipartFile file) {
        File tempFile = null;
        try {
            // 파일을 임시 파일로 저장
            tempFile = File.createTempFile("temp", "." + getFileExtension(file.getOriginalFilename()));
            file.transferTo(tempFile);

            // 기존 이미지 삭제(존재하면)
            deleteExistingImage(userId);

            // 파일 업로드
            String key = getImageKey(userId, getFileExtension(file.getOriginalFilename()));
            amazonS3.putObject(new PutObjectRequest(bucketName, key, tempFile));

            // URL 생성
            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(bucketName, key)
                            .withMethod(com.amazonaws.HttpMethod.GET)
                            .withExpiration(new Date(System.currentTimeMillis() + 3600 * 1000)); // 1시간 유효기간
            URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

            // 유저 엔티티에도 프로필 이미지 업로드
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UsersHandler(ErrorStatus.USER_NOT_FOUND));
            user.updateProfile(url.toString());
        } catch (IOException e) {
            throw new GeneralHandler(ErrorStatus.USER_PROFILE_UPLOAD_FAIL); // 사용자 정의 예외로 변환
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete(); // 임시 파일 삭제
            }
        }
    }

    private String getFileExtension(String fileName) {
        try {
            int lastIndexOfDot = fileName.lastIndexOf(".");
            if (lastIndexOfDot == -1 || lastIndexOfDot == fileName.length() - 1) {
                return ""; // 파일 확장자가 없는 경우
            }
            return fileName.substring(lastIndexOfDot + 1).toLowerCase(); // 파일 확장자 문자열 반환
        } catch (StringIndexOutOfBoundsException e) {
            throw new GeneralHandler(ErrorStatus.S3_FILE_NAME_ERROR);
        }
    }
}
