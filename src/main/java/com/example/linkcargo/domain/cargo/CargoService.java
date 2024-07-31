package com.example.linkcargo.domain.cargo;

import com.example.linkcargo.domain.cargo.dto.request.CargoRequest;
import com.example.linkcargo.global.response.code.resultCode.ErrorStatus;
import com.example.linkcargo.global.response.exception.handler.CargoHandler;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CargoService {

    private final CargoRepository cargoRepository;

    public Cargo createCargo(Long userId, CargoRequest cargoRequest) {
        Cargo cargo = cargoRequest.toEntity(userId);
        cargo.prePersist();

        return cargoRepository.save(cargo);
    }

    public Cargo getCargo(String cargoId) {
        Cargo cargo = cargoRepository.findById(cargoId)
            .orElseThrow(() -> new CargoHandler(ErrorStatus.INVALID_CARGO_INPUT));
        return cargo;
    }

    public List<Cargo> getMyCargos(Long userId) {
        List<Cargo> cargos = cargoRepository.findAllByUserId(userId);
        return cargos;
    }
}
