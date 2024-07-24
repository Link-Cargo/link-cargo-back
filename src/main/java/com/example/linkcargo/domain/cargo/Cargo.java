package com.example.linkcargo.domain.cargo;

import com.example.linkcargo.global.entity.BaseEntity;
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
public class Cargo extends BaseEntity {

    @Id
    private String id;

    private String additionalInstructions;
    private String friendlyDescription;
    private Boolean insuranceRequired;
    private CargoInfo cargoInfo;

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
