package com.example.linkcargo.domain.quotation;


import com.example.linkcargo.global.entity.JpaBaseEntity;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "quotations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quotation extends JpaBaseEntity {

    @Id
    private Long id;

    private String userId;

    private Freight freight;

    private Cost cost;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Freight {

        private Long scheduleId;

        private String remark;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Cost {

        private Long cargoId;

        private ChargeExport chargeExport;

        private FreightCost freightCost;

        private BigDecimal totalCost;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChargeExport {

        private TEU CIC;

        private TEU DO_FEE;

        private TEU HANDLING_FEE;

        private TEU CFS_CHARGE;

        private TEU LIFT_STATUS;

        private TEU CUSTOMS_CLEARANCE_FEE;

        private TEU WARFAGE_FEE;

        private TEU TRUCKING;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TEU {

        private String unit;
        private BigDecimal LCL;
        private String remark;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FreightCost {

        private BigDecimal LCL;
        private BigDecimal CBM;
        private BigDecimal SUM;
    }
}
