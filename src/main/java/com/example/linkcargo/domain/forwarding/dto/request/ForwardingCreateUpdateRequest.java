package com.example.linkcargo.domain.forwarding.dto.request;
import com.example.linkcargo.domain.forwarding.Forwarding;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;


public record ForwardingCreateUpdateRequest(
    @NotNull(message = "Firm Name is mandatory")
    String firmName,

    @NotNull(message = "Business Number is mandatory")
    @Pattern(regexp = "\\d{3}-\\d{2}-\\d{6}", message = "Business Number must be in the format XXX-XX-XXXXXX")
    String businessNumber,

    @NotNull(message = "Main Subject is mandatory")
    String mainSubject,

    @NotNull(message = "Firm URL is mandatory")
    @Pattern(regexp = "^(http://|https://).*", message = "Firm URL must start with http:// or https://")
    String firmUrl,

    @NotNull(message = "Firm Address is mandatory")
    String firmAddress,

    @NotNull(message = "Founded Year is mandatory")
    Integer foundedYear,

    @NotNull(message = "Firm CEO is mandatory")
    String firmCeo,

    @NotNull(message = "Firm Tel is mandatory")
    @Pattern(regexp = "\\d{2,3}-\\d{3,4}-\\d{4}", message = "Tel must be in the format XXX-XXX-XXXX or XXX-XXXX-XXXX")
    String firmTel
) {
    public Forwarding toEntity() {
        final String DEFAULT_PROFILE_IMAGE = "https://link-cargo-bucket.s3.ap-northeast-2.amazonaws.com/Image/example.PNG";

        return Forwarding.builder()
            .firmName(this.firmName)
            .businessNumber(this.businessNumber)
            .firmLogoImageUrl(DEFAULT_PROFILE_IMAGE)
            .mainSubject(this.mainSubject)
            .firmUrl(this.firmUrl)
            .firmAddress(this.firmAddress)
            .foundedYear(this.foundedYear)
            .firmCeo(this.firmCeo)
            .firmTel(this.firmTel)
            .build();
    }

}
