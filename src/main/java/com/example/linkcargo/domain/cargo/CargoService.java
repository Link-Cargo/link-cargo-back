package com.example.linkcargo.domain.cargo;

import com.example.linkcargo.domain.cargo.dto.request.CargoRequest;
import com.example.linkcargo.domain.cargo.dto.response.CargoPageResponse;
import com.example.linkcargo.global.response.code.resultCode.ErrorStatus;
import com.example.linkcargo.global.response.exception.handler.CargoHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CargoService {

    private final CargoRepository cargoRepository;

    /**
     * 화물 생성
     */
    public Cargo createCargo(Long userId, CargoRequest cargoRequest) {
        Cargo cargo = cargoRequest.toEntity(userId);
        cargo.prePersist();

        return cargoRepository.save(cargo);
    }

    /**
     * 화물 조회
     */
    public Cargo getCargo(String cargoId) {
        Cargo cargo = cargoRepository.findById(cargoId)
            .orElseThrow(() -> new CargoHandler(ErrorStatus.CARGO_NOT_FOUND));
        return cargo;
    }

    /**
     * 내 화물 목록 조회
     */
    public CargoPageResponse getMyCargos(Long userId, PageRequest pageRequest) {
        Page<Cargo> cargos = cargoRepository.findAllByUserId(userId, pageRequest);
        return new CargoPageResponse(
            cargos.getContent(), // 현재 페이지에 포함된 Cargo 객체들의 리스트
            cargos.getTotalElements(), // 전체 데이터의 총 개수
            cargos.getNumber(), // 현재 페이지의 페이지 번호
            cargos.getNumberOfElements() // 현재 페이지에 포함된 항목의 수
        );
    }

    /**
     * 내 화물 수정
     */
    @Transactional
    public Cargo updateMyCargo(Long userId, String cargoId, CargoRequest cargoRequest) {
        Cargo cargo = cargoRepository.findById(cargoId)
            .orElseThrow(() -> new CargoHandler(ErrorStatus.CARGO_NOT_FOUND));
        if(!cargo.getUserId().equals(userId)){
            throw new CargoHandler(ErrorStatus.CARGO_USER_NOT_MATCH);
        }
        cargo.update(cargoRequest);
        cargoRepository.save(cargo);
        return cargo;
    }

    /**
     * 내 화물 삭제
     */
    public void deleteMyCargo(Long userId, String cargoId) {
        Cargo cargo = cargoRepository.findById(cargoId)
            .orElseThrow(() -> new CargoHandler(ErrorStatus.CARGO_NOT_FOUND));
        if(!cargo.getUserId().equals(userId)){
            throw new CargoHandler(ErrorStatus.CARGO_USER_NOT_MATCH);
        }
        cargoRepository.delete(cargo);
    }

    /**
     * 내 화물 수정
     */
}
