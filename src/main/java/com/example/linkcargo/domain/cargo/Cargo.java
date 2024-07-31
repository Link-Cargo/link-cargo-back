package com.example.linkcargo.domain.cargo;

import com.example.linkcargo.domain.cargo.dto.request.CargoRequest;
import com.example.linkcargo.global.entity.MongoBaseEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

import java.math.BigDecimal;

@Document(collection = "cargos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cargo extends MongoBaseEntity{

    @Id
    private String id;
    private Long userId;

    private String additionalInstructions;
    private String friendlyDescription;
    private Boolean insuranceRequired;
    private CargoInfo cargoInfo;

    public void update(CargoRequest cargoRequest) {
        // 기본 필드 업데이트
        this.additionalInstructions = cargoRequest.getAdditionalInstructions();
        this.friendlyDescription = cargoRequest.getFriendlyDescription();
        this.insuranceRequired = cargoRequest.getInsuranceRequired();

        // CargoInfo 업데이트
        CargoRequest.CargoInfoDto cargoInfoDto = cargoRequest.getCargoInfo();
        if (cargoInfoDto != null) {
            if (this.cargoInfo == null) {
                this.cargoInfo = new CargoInfo();
            }
            this.cargoInfo.productName = cargoInfoDto.getProductName();
            this.cargoInfo.hsCode = cargoInfoDto.getHsCode();
            this.cargoInfo.incoterms = cargoInfoDto.getIncoterms();
            this.cargoInfo.weight = cargoInfoDto.getWeight();
            this.cargoInfo.value = cargoInfoDto.getValue();
            this.cargoInfo.quantity = cargoInfoDto.getQuantity();

            // BoxSize 업데이트
            CargoRequest.BoxSizeDto boxSizeDto = cargoInfoDto.getBoxSize();
            if (boxSizeDto != null) {
                if (this.cargoInfo.boxSize == null) {
                    this.cargoInfo.boxSize = new BoxSize();
                }
                this.cargoInfo.boxSize.width = boxSizeDto.getWidth();
                this.cargoInfo.boxSize.height = boxSizeDto.getHeight();
                this.cargoInfo.boxSize.depth = boxSizeDto.getDepth();
            }
        }
    }



    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CargoInfo {
        private String productName;
        private String hsCode;
        private String incoterms;
        private BigDecimal weight;
        private BigDecimal value;
        private Integer quantity;
        private BoxSize boxSize;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BoxSize {
        private BigDecimal width;
        private BigDecimal height;
        private BigDecimal depth;
    }

}
