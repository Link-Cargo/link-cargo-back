package com.example.linkcargo.domain.dashboard.dto.response;

import com.example.linkcargo.domain.quotation.dto.response.QuotationInfoResponse;
import com.example.linkcargo.domain.user.User;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record DashboardQuotationResponse(
    String firmName,
    BigDecimal totalCost,
    String forwarderName,
    String forwarderEmail,
    String forwarderTel,
    QuotationInfoResponse quotationInfoResponse
) {
    public static DashboardQuotationResponse fromEntity(
        User user,
        QuotationInfoResponse quotationInfoResponse,
        BigDecimal totalCost
    ) {
        String fullName = user.getFirstName() + " " + user.getLastName();
        return DashboardQuotationResponse.builder()
            .firmName(user.getCompanyName())
            .totalCost(totalCost)
            .forwarderName(fullName)
            .forwarderEmail(user.getEmail())
            .forwarderEmail(user.getPhoneNumber())
            .quotationInfoResponse(quotationInfoResponse)
            .build();
    }

}
