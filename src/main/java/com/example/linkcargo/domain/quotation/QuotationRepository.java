package com.example.linkcargo.domain.quotation;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuotationRepository extends MongoRepository<Quotation, String> {

    boolean existsByConsignorIdAndFreight_ScheduleIdAndQuotationStatus(
        String consignorId,
        String scheduleId,
        QuotationStatus quotationStatus
    );

    List<Quotation> findQuotationsByConsignorId(String s);

    List<Quotation> findQuotationsByIdOrOriginalQuotationId(String quotationId, String originalQuotationId);


    Optional<Quotation> findQuotationById(String id);


    List<Quotation> findByConsignorIdAndQuotationStatus(String string, QuotationStatus quotationStatus);

    List<Quotation> findQuotationsByRawQuotationIdAndQuotationStatus(String rawQuotationId, QuotationStatus quotationStatus);
}
