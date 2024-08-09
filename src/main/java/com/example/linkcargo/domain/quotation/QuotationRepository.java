package com.example.linkcargo.domain.quotation;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuotationRepository extends MongoRepository<Quotation, String> {

    boolean existsByUserIdAndCost_CargoIdAndFreight_ScheduleId(String userId, String cargoId, String scheduleId);
}
