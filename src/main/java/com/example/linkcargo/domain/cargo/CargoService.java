package com.example.linkcargo.domain.cargo;

import com.example.linkcargo.domain.cargo.CargoCostCalculator.CargoInfo;
import com.example.linkcargo.domain.cargo.dto.CargoDTO;
import com.example.linkcargo.domain.cargo.dto.request.CargoRequest;
import com.example.linkcargo.domain.cargo.dto.request.CargosRequest;
import com.example.linkcargo.domain.cargo.dto.response.CargoIdsResponse;
import com.example.linkcargo.domain.cargo.dto.response.CargoPageResponse;
import com.example.linkcargo.domain.cargo.dto.response.CargoResponse;
import com.example.linkcargo.domain.port.Port;
import com.example.linkcargo.domain.port.PortRepository;
import com.example.linkcargo.domain.prediction.Prediction;
import com.example.linkcargo.domain.prediction.PredictionRepository;
import com.example.linkcargo.domain.quotation.Quotation;
import com.example.linkcargo.global.response.code.resultCode.ErrorStatus;
import com.example.linkcargo.global.response.exception.handler.CargoHandler;
import com.example.linkcargo.global.response.exception.handler.GeneralHandler;
import com.example.linkcargo.global.response.exception.handler.QuotationHandler;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
    private final PortRepository portRepository;
    private final PredictionRepository predictionRepository;

    /**
     * 화물 여러 개 추가
     */
    @Transactional
    public CargoIdsResponse createCargos(Long userId, CargosRequest cargosRequest) {
        List<String> savedCargoIds = new ArrayList<>();
        for (CargoRequest cargoRequest : cargosRequest.cargos()) {
            Cargo cargo = cargoRequest.toEntity(
                userId,
                cargosRequest.exportPortId(),
                cargosRequest.importPortId(),
                cargosRequest.wishExportDate(),
                cargosRequest.incoterms()
            );
            cargo.prePersist();

            Cargo savedCargo = cargoRepository.save(cargo);
            savedCargoIds.add(savedCargo.getId());
        }
        return new CargoIdsResponse(savedCargoIds);
    }

    /**
     * 화물 조회
     */
    public CargoResponse getCargo(String cargoId) {
        Cargo cargo = cargoRepository.findById(cargoId)
            .orElseThrow(() -> new CargoHandler(ErrorStatus.CARGO_NOT_FOUND));
        Port exportPort = portRepository.findById(cargo.getExportPortId()).get();
        Port importPort = portRepository.findById(cargo.getImportPortId()).get();
        return new CargoResponse(cargo.toCargoDTO(exportPort, importPort));
    }

    /**
     * 내 화물 목록 조회
     */
    public CargoPageResponse getMyCargos(Long userId, PageRequest pageRequest) {
        Page<Cargo> cargos = cargoRepository.findAllByUserId(userId, pageRequest);
        return new CargoPageResponse(
            makeCargoDTOList(cargos.getContent()), // 현재 페이지에 포함된 Cargo 객체(CargoDTO 롤 변환한)들의 리스트
            cargos.getTotalElements(), // 전체 데이터의 총 개수
            cargos.getNumber(), // 현재 페이지의 페이지 번호
            cargos.getNumberOfElements() // 현재 페이지에 포함된 항목의 수
        );
    }

    private List<CargoDTO> makeCargoDTOList(List<Cargo> cargos) {
        List<CargoDTO> cargoDTOS = new ArrayList<>();
        for (Cargo cargo : cargos) {
            Port exportPort = portRepository.findById(cargo.getExportPortId()).get();
            Port importPort = portRepository.findById(cargo.getImportPortId()).get();
            cargoDTOS.add(cargo.toCargoDTO(exportPort, importPort));
        }
        return cargoDTOS;
    }

    /**
     * 내 화물 수정
     */
    @Transactional
    public Cargo updateMyCargo(Long userId, String cargoId, CargoRequest cargoRequest) {
        Cargo cargo = cargoRepository.findById(cargoId)
            .orElseThrow(() -> new CargoHandler(ErrorStatus.CARGO_NOT_FOUND));
        if (!cargo.getUserId().equals(userId)) {
            throw new CargoHandler(ErrorStatus.CARGO_USER_NOT_MATCH);
        }
        cargo.update(cargoRequest);
        cargo.preUpdate();
        cargoRepository.save(cargo);
        return cargo;
    }

    /**
     * 내 화물 삭제
     */
    public void deleteMyCargo(Long userId, String cargoId) {
        Cargo cargo = cargoRepository.findById(cargoId)
            .orElseThrow(() -> new CargoHandler(ErrorStatus.CARGO_NOT_FOUND));
        if (!cargo.getUserId().equals(userId)) {
            throw new CargoHandler(ErrorStatus.CARGO_USER_NOT_MATCH);
        }
        cargoRepository.delete(cargo);
    }

    public BigDecimal calculateCostByCargos(CargosRequest request) {
        List<CargoInfo> cargoInfos = request.cargos().stream()
            .map(CargoCostCalculator::convertToCargoInfo)
            .toList();

        LocalDate currentDate = LocalDate.from(request.wishExportDate());
        String currentYear = String.valueOf(currentDate.getYear());
        String currentMonth = String.format("%02d", currentDate.getMonthValue());
        Optional<Prediction> currentPrediction = predictionRepository.findByYearAndMonth(Integer.parseInt(currentYear), Integer.parseInt(currentMonth));
        String freightCostIndex;
        ;
        if (currentPrediction.isPresent()) {
            freightCostIndex = currentPrediction.get().getFreightCostIndex();
        } else {
            throw new GeneralHandler(ErrorStatus.PREDICTION_NOT_FOUND);
        }

        Integer freightCost = Integer.parseInt(freightCostIndex) / 10 - 5;
        System.out.println(freightCost);

        return CargoCostCalculator.calculateTotalCost(cargoInfos, request.incoterms(), freightCost);
    }


}
