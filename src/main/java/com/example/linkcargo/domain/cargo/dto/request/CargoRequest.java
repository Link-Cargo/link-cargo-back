package com.example.linkcargo.domain.cargo.dto.request;

import com.example.linkcargo.domain.cargo.Cargo;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
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

    @Size(max = 255, message = "Additional instructions must be less than 255 characters")
    private String additionalInstructions;

    @NotBlank(message = "Friendly description is required")
    @Size(max = 255, message = "Friendly description must be less than 255 characters")
    private String friendlyDescription;

    @NotNull(message = "Insurance requirement must be specified")
    private Boolean insuranceRequired;

    @NotNull(message = "Cargo info is required")
    private CargoInfoDto cargoInfo;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CargoInfoDto {
        @NotBlank(message = "Product name is required")
        @Size(max = 255, message = "Product name must be less than 255 characters")
        private String productName;

        @NotBlank(message = "HS Code is required")
        @Size(max = 12, message = "HS Code must be less than 12 characters")
        private String hsCode;

        @NotBlank(message = "Incoterms is required")
        @Size(max = 20, message = "Incoterms must be less than 20 characters")
        private String incoterms;

        @NotNull(message = "Weight is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Weight must be greater than zero")
        private BigDecimal weight;

        @NotNull(message = "Value is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Value must be greater than zero")
        private BigDecimal value;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;

        @NotNull(message = "Box size is required")
        private BoxSizeDto boxSize;

        public Cargo.CargoInfo toEntity() {
            return Cargo.CargoInfo.builder()
                .productName(this.productName)
                .hsCode(this.hsCode)
                .incoterms(this.incoterms)
                .weight(this.weight)
                .value(this.value)
                .quantity(this.quantity)
                .boxSize(this.boxSize.toEntity())
                .build();
        }
    }

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

    public Cargo toEntity(Long userId) {
        return Cargo.builder()
            .userId(userId)
            .additionalInstructions(this.additionalInstructions)
            .friendlyDescription(this.friendlyDescription)
            .insuranceRequired(this.insuranceRequired)
            .cargoInfo(this.cargoInfo.toEntity())
            .build();
    }
}