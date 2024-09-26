package com.example.linkcargo.domain.dashboard.dto.response;

import com.example.linkcargo.domain.cargo.Cargo;
import com.example.linkcargo.domain.port.Port;
import com.example.linkcargo.domain.quotation.Quotation;
import com.example.linkcargo.domain.schedule.Schedule;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record DashboardRawQuotationResponse(

    List<RawQuotationInfo> rawQuotationInfoList

) {
    @Builder
    public record RawQuotationInfo(
        String rawQuotationId,
        String exportPort,
        String importPort,
        LocalDate ETD,
        LocalDate requestDate
    ){
        public static RawQuotationInfo fromEntity(Quotation quotation, Cargo cargo, Port exportPort, Port importPort){
            return RawQuotationInfo.builder()
                .rawQuotationId(quotation.getId())
                .exportPort(exportPort.getName())
                .importPort(importPort.getName())
                .ETD(LocalDate.from(cargo.getWishExportDate()))
                .requestDate(quotation.getCreatedAt().toLocalDate())
                .build();
        }
    }
    public static DashboardRawQuotationResponse fromEntity(
        List<RawQuotationInfo> rawQuotationInfoList) {
        return DashboardRawQuotationResponse.builder()
            .rawQuotationInfoList(rawQuotationInfoList)
            .build();
    }
}
