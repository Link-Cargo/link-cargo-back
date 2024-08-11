package com.example.linkcargo.domain.image;


import com.example.linkcargo.domain.user.User;
import com.example.linkcargo.domain.user.UserRepository;
import com.example.linkcargo.global.response.code.resultCode.ErrorStatus;
import com.example.linkcargo.global.response.exception.handler.UsersHandler;
import com.example.linkcargo.global.s3.S3Service;
import com.example.linkcargo.global.s3.dto.S3Result;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageService {
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    @Transactional
    public List<Image> uploadImage(List<MultipartFile> fileList, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsersHandler(
            ErrorStatus.USER_NOT_FOUND));

        List<Image> savedImages = new ArrayList<>();
        for(MultipartFile file : fileList) {
            S3Result s3Result = s3Service.uploadFile(file);

            Image newImage = Image.builder()
                .name(file.getOriginalFilename())
                .url(s3Result.fileUrl())
                .type(file.getContentType())
                .user(user)
                .build();

            savedImages.add(imageRepository.save(newImage));
        }

        return savedImages;
    }


    public List<String> selectRandomImages(String keyword, int count) {
        List<String> allUrls = imageRepository.findUrlsByNameContaining(keyword);

        List<String> result = new ArrayList<>(count);
        String lastUsedUrl = null;

        for (int i = 0; i < count; i++) {
            String selectedUrl;
            do {
                selectedUrl = allUrls.get((int) (Math.random() * allUrls.size()));
            } while (selectedUrl.equals(lastUsedUrl));

            result.add(selectedUrl);
            lastUsedUrl = selectedUrl;
        }

        return result;
    }
}
