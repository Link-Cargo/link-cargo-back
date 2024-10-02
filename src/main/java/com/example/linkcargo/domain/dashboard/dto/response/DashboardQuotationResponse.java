package com.example.linkcargo.domain.dashboard.dto.response;

import com.example.linkcargo.domain.quotation.dto.response.QuotationInfoResponse;
import com.example.linkcargo.domain.user.User;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record DashboardQuotationResponse(
    String firmName,
    BigDecimal totalCost,
    Integer forwarderId,
    String forwarderName,
    String forwarderEmail,
    String forwarderTel,
    String particulars,
    QuotationInfoResponse quotationInfoResponse
) {
    public static DashboardQuotationResponse fromEntity(
        User user,
        QuotationInfoResponse quotationInfoResponse,
        BigDecimal totalCost,
        String particulars
    ) {
        String fullName = user.getFirstName() + " " + user.getLastName();
        return DashboardQuotationResponse.builder()
            .firmName(user.getCompanyName())
            .forwarderId(Math.toIntExact(user.getId()))
            .totalCost(totalCost)
            .forwarderName(fullName)
            .forwarderEmail(user.getEmail())
            .forwarderTel(user.getPhoneNumber())
            .particulars(particulars)
            .quotationInfoResponse(quotationInfoResponse)
            .build();
    }

}
