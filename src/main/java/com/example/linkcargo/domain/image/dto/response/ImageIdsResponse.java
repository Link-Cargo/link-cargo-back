package com.example.linkcargo.domain.image.dto.response;

import com.example.linkcargo.domain.image.Image;
import java.util.List;
import lombok.Builder;

@Builder
public record ImageIdsResponse(
    List<Long> imageIds
) {
    public static ImageIdsResponse fromImages(List<Image> images) {
        List<Long> imageIds = images.stream()
            .map(Image::getId)
            .toList();

        return ImageIdsResponse.builder()
            .imageIds(imageIds)
            .build();
    }
}
