package com.example.linkcargo.domain.quotation;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuotationRepository extends MongoRepository<Quotation, String> {

    boolean existsByConsignorIdAndFreight_ScheduleId(String consignorId, String scheduleId);

    List<Quotation> findQuotationsByConsignorId(String s);

    List<Quotation> findQuotationsByConsignorIdAndFreight_ScheduleIdAndQuotationStatus(String consignorId, String scheduleId, QuotationStatus quotationStatus);
}
