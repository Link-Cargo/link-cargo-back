package com.example.linkcargo.domain.cargo;

import com.example.linkcargo.domain.cargo.dto.CargoDTO;
import com.example.linkcargo.domain.cargo.dto.request.CargoRequest;
import com.example.linkcargo.domain.port.Port;
import com.example.linkcargo.global.entity.MongoBaseEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private LocalDateTime wishExportDate;
    private String incoterms;

    private CargoInfo cargoInfo;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CargoInfo {

        private String productName;
        private String hsCode;
        private String additionalNotes;
        private Integer totalQuantity;
        private Integer quantityPerBox;
        private BoxSize boxSize;
        private BigDecimal weight;
        private BigDecimal value;
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
        CargoDTO.CargoInfoDto cargoInfoDto = new CargoDTO.CargoInfoDto(
            this.cargoInfo.getProductName(),
            this.cargoInfo.getHsCode(),
            this.cargoInfo.getAdditionalNotes(),
            this.cargoInfo.getTotalQuantity(),
            this.cargoInfo.getQuantityPerBox(),
            new CargoDTO.BoxSizeDto(
                this.cargoInfo.getBoxSize().getWidth(),
                this.cargoInfo.getBoxSize().getHeight(),
                this.cargoInfo.getBoxSize().getDepth()
            ),
            this.cargoInfo.getWeight(),
            this.cargoInfo.getValue()
        );

        return new CargoDTO(
            this.id,
            this.userId,
            exportPort,
            importPort,
            this.wishExportDate,
            this.incoterms,
            cargoInfoDto
        );
    }
    public void update (CargoRequest cargoRequest){
        this.cargoInfo = CargoInfo.builder()
            .productName(cargoRequest.getProductName())
            .hsCode(cargoRequest.getHsCode())
            .additionalNotes(cargoRequest.getAdditionalNotes())
            .totalQuantity(cargoRequest.getTotalQuantity())
            .quantityPerBox(cargoRequest.getQuantityPerBox())
            .boxSize(cargoRequest.getBoxSize().toEntity())
            .weight(cargoRequest.getWeight())
            .value(cargoRequest.getValue())
            .build();
    }
}
