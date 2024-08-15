package com.example.linkcargo.domain.cargo.dto.request;

import com.example.linkcargo.domain.cargo.Cargo;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CargoRequest {

    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Product name must be less than 255 characters")
    private String productName; // 화물명

    @NotBlank(message = "HS Code is required")
    @Size(max = 12, message = "HS Code must be less than 12 characters")
    private String hsCode; // HS 코드

    @NotNull(message = "Total quantity is required")
    @Min(value = 1, message = "Total quantity must be at least 1")
    private Integer totalQuantity; // 총 수출 물품 수량

    @NotNull(message = "Quantity per box is required")
    @Min(value = 1, message = "Quantity per box must be at least 1")
    private Integer quantityPerBox; // 박스 당 물품 수량

    @NotNull(message = "Box size is required")
    private BoxSizeDto boxSize; // 박스 가로/세로/높이

    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Weight must be greater than zero")
    private BigDecimal weight; // 박스 중량

    @NotNull(message = "Value is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Value must be greater than zero")
    private BigDecimal value; // 물품 가액

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BoxSizeDto {

        @NotNull(message = "Width is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Width must be greater than zero")
        private BigDecimal width;

        @NotNull(message = "Height is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Height must be greater than zero")
        private BigDecimal height;

        @NotNull(message = "Depth is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Depth must be greater than zero")
        private BigDecimal depth;

        public Cargo.BoxSize toEntity() {
            return Cargo.BoxSize.builder()
                .width(this.width)
                .height(this.height)
                .depth(this.depth)
                .build();
        }
    }

    // Cargo 엔티티로 변환하는 메서드
    public Cargo toEntity(Long userId, Long exportPortId, Long importPortId, LocalDateTime wishExportDate, String incoterms) {
        return Cargo.builder()
            .userId(userId)
            .exportPortId(exportPortId)
            .importPortId(importPortId)
            .wishExportDate(wishExportDate)
            .incoterms(incoterms)
            .cargoInfo(Cargo.CargoInfo.builder()
                .productName(this.productName)
                .hsCode(this.hsCode)
                .totalQuantity(this.totalQuantity)
                .quantityPerBox(this.quantityPerBox)
                .boxSize(this.boxSize.toEntity())
                .weight(this.weight)
                .value(this.value)
                .build())
            .build();
    }
}
