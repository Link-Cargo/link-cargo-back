package com.example.linkcargo.domain.quotation;

import com.example.linkcargo.global.entity.MongoBaseEntity;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "quotations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quotation extends MongoBaseEntity {

    @Id
    private String id;

    @Indexed
    private String forwarderId;

    @Indexed
    private String consignorId;

    private QuotationStatus quotationStatus;

    private Freight freight;

    private Cost cost;

    private String particulars;

    private String originalQuotationId;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Freight {

        @Indexed
        private String scheduleId;

        private String remark;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Cost {

        @Indexed
        private List<String> cargoIds;

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

        private TEU THC;

        private TEU CIC;

        private TEU DO_FEE;

        private TEU HANDLING_FEE;

        private TEU CFS_CHARGE;

        private TEU LIFT_STATUS;

        private TEU CUSTOMS_CLEARANCE_FEE;

        private TEU WARFAGE_FEE;

        private TEU TRUCKING;

        private BigDecimal SUM;
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
