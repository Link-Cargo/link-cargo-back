package com.example.linkcargo.domain.cargo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CargoRepository extends MongoRepository<Cargo, String> {

}
