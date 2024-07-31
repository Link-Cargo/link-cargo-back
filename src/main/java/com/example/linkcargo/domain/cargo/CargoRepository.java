package com.example.linkcargo.domain.cargo;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CargoRepository extends MongoRepository<Cargo, String> {

    Page<Cargo> findAllByUserId(Long userId, PageRequest pageRequest);
    List<Cargo> findAllByUserId(Long userId);
}
