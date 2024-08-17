package com.example.linkcargo.domain.user;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.linkcargo.domain.user.dto.response.FileResponse;
import com.example.linkcargo.domain.user.dto.response.FilesResponse;
import com.example.linkcargo.global.response.code.resultCode.ErrorStatus;
import com.example.linkcargo.global.response.exception.handler.GeneralHandler;
import com.example.linkcargo.global.response.exception.handler.UsersHandler;
import com.example.linkcargo.global.s3.dto.FileDTO;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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

    @Value("${cloud.aws.s3.profile-bucket.name}")
    private String profileBucketName;

    @Value("${cloud.aws.s3.chatroom-bucket.name}")
    private String chatroomBucketName;

    private String region = "ap-northeast-2";

    private String getImageKey(Long userId, String fileExtension) {
        return userId + "." + fileExtension;
    }

    @Transactional
    public void deleteExistingImage(Long userId) {
        // 유저 ID를 파일명으로 갖는 모든 확장자의 파일을 삭제
        String[] extensions = {"jpg", "jpeg", "png" };  // 삭제할 확장자 목록
        for (String extension : extensions) {
            String key = getImageKey(userId, extension);
            if (amazonS3.doesObjectExist(profileBucketName, key)) {
                amazonS3.deleteObject(new DeleteObjectRequest(profileBucketName, key));
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
            tempFile = File.createTempFile("temp",
                "." + getFileExtension(file.getOriginalFilename()));
            file.transferTo(tempFile);

            // 기존 이미지 삭제(존재하면)
            deleteExistingImage(userId);

            // 파일 업로드
            String key = getImageKey(userId, getFileExtension(file.getOriginalFilename()));
            amazonS3.putObject(new PutObjectRequest(profileBucketName, key, tempFile));

            // URL 생성
            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(profileBucketName, key)
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

    @Transactional
    public FileResponse uploadFile(MultipartFile file, Long chatRoomId, Long userId) {
        File tempFile = null;
        try {
            // 파일 확장자 가져오기
            String fileExtension = getFileExtension(file.getOriginalFilename());

            // 임시 파일 생성
            tempFile = File.createTempFile("temp", "." + fileExtension);

            // MultipartFile을 임시 파일로 저장
            file.transferTo(tempFile);

            // 파일 업로드를 위한 UUID 생성 및 키 생성
            String uuidString = UUID.randomUUID().toString();
            String key = chatRoomId + "/" + uuidString + "/" + file.getOriginalFilename();

            // S3에 파일 업로드
            amazonS3.putObject(new PutObjectRequest(chatroomBucketName, key, tempFile));

            // 객체의 LastModified 정보를 얻기 위한 객체 메타데이터 조회
            S3Object s3Object = amazonS3.getObject(new GetObjectRequest(chatroomBucketName, key));
            Date lastModifiedDate = s3Object.getObjectMetadata().getLastModified();

            // LocalDateTime으로 변환
            LocalDateTime lastModified = Instant.ofEpochMilli(lastModifiedDate.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

            // 유효기간이 1시간인 presigned URL 생성
            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(
                chatroomBucketName, key)
                .withMethod(com.amazonaws.HttpMethod.GET)
                .withExpiration(new Date(System.currentTimeMillis() + 3600 * 1000)); // 1시간 유효기간
            URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

            // FileResponse 생성
            return new FileResponse(
                new FileDTO(
                    file.getOriginalFilename(), url.toString(),
                    lastModified.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                )
            );
        } catch (IOException e) {
            // 예외 발생 시 트랜잭션 롤백 및 사용자 정의 예외 던지기
            throw new GeneralHandler(ErrorStatus.USER_PROFILE_UPLOAD_FAIL);
        } finally {
            if (tempFile != null && tempFile.exists()) {
                if (!tempFile.delete()) {
                    // 파일 삭제에 실패한 경우 로그 출력
                    System.err.println(
                        "Failed to delete temporary file: " + tempFile.getAbsolutePath());
                }
            }
        }
    }

    /**
     * 특정 폴더(프리픽스) 아래의 모든 객체를 조회
     */
    public FilesResponse getAllObjectsInChatRoom(Long chatRoomId) {
        List<FileDTO> fileDTOS = new ArrayList<>();

        // 객체 나열
        ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request()
            .withBucketName(chatroomBucketName)
            .withPrefix(chatRoomId.toString() + "/");

        ListObjectsV2Result result;
        do {
            result = amazonS3.listObjectsV2(listObjectsRequest);
            for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                String objectKey = objectSummary.getKey();
                // 마지막 '/' 이후의 부분 추출
                String fileName = objectKey.substring(objectKey.lastIndexOf('/') + 1);
                // 객체의 마지막 수정 시간
                Date lastModifiedDate = objectSummary.getLastModified();
                // Date를 LocalDateTime으로 변환
                LocalDateTime lastModified = Instant.ofEpochMilli(lastModifiedDate.getTime())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
                // URL 생성
                String url = String.format("https://%s.s3.%s.amazonaws.com/%s", chatroomBucketName,
                    region, objectKey);
                fileDTOS.add(new FileDTO(fileName, url, lastModified.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
            }

            // 다음 페이지가 있는 경우, 계속 나열
            listObjectsRequest.setContinuationToken(result.getNextContinuationToken());

        } while (result.isTruncated()); // 객체가 더 있는 경우

        return new FilesResponse(fileDTOS);
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
