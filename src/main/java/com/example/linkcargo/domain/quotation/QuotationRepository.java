package com.example.linkcargo.domain.quotation;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuotationRepository extends MongoRepository<Quotation, String> {

    boolean existsByConsignorIdAndCost_CargoIdAndFreight_ScheduleId(String consignorId, String cargoId, String scheduleId);
}
