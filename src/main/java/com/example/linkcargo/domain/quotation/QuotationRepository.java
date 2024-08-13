package com.example.linkcargo.domain.quotation;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuotationRepository extends MongoRepository<Quotation, String> {

    boolean existsByConsignorIdAndFreight_ScheduleId(String s, String s1);
}
