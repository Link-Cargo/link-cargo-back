package com.example.linkcargo.domain.cargo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CargoRepository extends MongoRepository<Cargo, String> {

    Page<Cargo> findAllByUserId(Long userId, PageRequest pageRequest);
}
