package com.example.linkcargo.cargo;

import static org.assertj.core.api.Assertions.*;

import com.example.linkcargo.domain.cargo.Cargo;
import com.example.linkcargo.domain.cargo.CargoRepository;
import com.example.linkcargo.domain.cargo.dto.request.CargoRequest;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class cargoRepositoryTest {

    @Autowired
    CargoRepository cargoRepository;
    private Cargo cargo;
    @BeforeEach
    void setUp() {
        // BoxSizeDto 객체 생성
        CargoRequest.BoxSizeDto boxSizeDto = new CargoRequest.BoxSizeDto(new BigDecimal("15.0"), new BigDecimal("10.0"), new BigDecimal("5.0"));
        // CargoInfoDto 객체 생성
        CargoRequest.CargoInfoDto cargoInfoDto = new CargoRequest.CargoInfoDto("Smartphone", "8517", "FOB", new BigDecimal("0.25"), new BigDecimal("299.99"), 50, boxSizeDto);
        // CargoRequest 객체 생성
        CargoRequest cargoRequest = new CargoRequest("Handle with care. Fragile.", "A shipment of electronic components.", true, cargoInfoDto);
        cargo = cargoRequest.toEntity(1L);
    }
    @Test
    void 화물_정상_저장() {
        Cargo savedCargo = cargoRepository.save(cargo);
        Assertions.assertAll(
            () -> assertThat(savedCargo.getUserId()).isEqualTo(cargo.getUserId()),
            () -> assertThat(savedCargo.getCargoInfo()).isEqualTo(cargo.getCargoInfo()),
            () -> assertThat(savedCargo.getCargoInfo().getBoxSize()).isEqualTo(cargo.getCargoInfo().getBoxSize())
        );
    }

    @Test
    void 나의_화물_조회() {
        List<Cargo> cargos = cargoRepository.findAllByUserId(1L);
    }
}
