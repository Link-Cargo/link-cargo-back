package com.example.linkcargo.domain.cargo;

import com.example.linkcargo.domain.cargo.dto.request.CargoRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CargoService {

    private final CargoRepository cargoRepository;

    public Cargo createCargo(CargoRequest cargoRequest) {
        Cargo cargo = cargoRequest.toEntity();
        cargo.prePersist();

        return cargoRepository.save(cargo);
    }
}
