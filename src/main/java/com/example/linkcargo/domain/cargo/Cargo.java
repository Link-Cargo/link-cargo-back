package com.example.linkcargo.domain.cargo;

import com.example.linkcargo.domain.cargo.dto.CargoDTO;
import com.example.linkcargo.domain.cargo.dto.request.CargoRequest;
import com.example.linkcargo.domain.port.Port;
import com.example.linkcargo.global.entity.MongoBaseEntity;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "cargos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cargo extends MongoBaseEntity {

    @Id
    private String id;
    private Long userId;

    private Long exportPortId;
    private Long importPortId;

    private String additionalInstructions;
    private String friendlyDescription;
    private Boolean insuranceRequired;
    private CargoInfo cargoInfo;

    public void update(CargoRequest cargoRequest) {
        // 기본 필드 업데이트
        this.exportPortId = cargoRequest.getExportPortId();
        this.importPortId = cargoRequest.getImportPortId();
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

    public CargoDTO toCargoDTO(Port exportPort, Port importPort) {
        CargoDTO.CargoInfoDto cargoInfoDto = null;
        if (this.cargoInfo != null) {
            CargoDTO.BoxSizeDto boxSizeDto = null;
            if (this.cargoInfo.getBoxSize() != null) {
                boxSizeDto = new CargoDTO.BoxSizeDto(
                    this.cargoInfo.getBoxSize().getWidth(),
                    this.cargoInfo.getBoxSize().getHeight(),
                    this.cargoInfo.getBoxSize().getDepth()
                );
            }
            cargoInfoDto = new CargoDTO.CargoInfoDto(
                this.cargoInfo.getProductName(),
                this.cargoInfo.getHsCode(),
                this.cargoInfo.getIncoterms(),
                this.cargoInfo.getWeight(),
                this.cargoInfo.getValue(),
                this.cargoInfo.getQuantity(),
                boxSizeDto
            );
        }

        return new CargoDTO(
            exportPort,
            importPort,
            this.additionalInstructions,
            this.friendlyDescription,
            this.insuranceRequired,
            cargoInfoDto
        );
    }
}
