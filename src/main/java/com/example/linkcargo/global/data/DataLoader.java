package com.example.linkcargo.global.data;

import com.example.linkcargo.domain.cargo.CargoService;
import com.example.linkcargo.domain.port.PortRepository;
import com.example.linkcargo.domain.token.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("!test") // 테스트 환경에서는 동작하지 않도록
public class DataLoader {

    private final RefreshTokenService refreshTokenService;
    private final PortRepository portRepository;
    private final CargoService cargoService;

//    @PostConstruct
//    public void init() {
    // USER - 데이터 넣어지긴 하는데 로그인 시 해당 유저를 DB 애서 못찾음
//        UserRegisterRequest userRegisterRequest = new UserRegisterRequest(
//            Role.CONSIGNOR, // 예시 역할
//            "John", // 예시 이름
//            "Doe", // 예시 성
//            "john.doe@example.com", // 예시 이메일
//            "SecureP@ssw0rd", // 예시 비밀번호
//            "+1234567890", // 예시 전화번호
//            "Tech Corp", // 예시 회사명
//            "Software Engineer", // 예시 직책
//            "1234567890" // 예시 사업자 번호
//        );
//        User joinedUser = refreshTokenService.join(userRegisterRequest);

    // PORT
//        Port exportPort = Port.builder()
//            .name("Export Port A") // 포트 이름 설정
//            .type(PortType.EXPORT) // 포트 타입 설정 (예: EXPORT)
//            .build();
//
//        Port importPort = Port.builder()
//            .name("Import Port B") // 포트 이름 설정
//            .type(PortType.IMPORT) // 포트 타입 설정 (예: IMPORT)
//            .build();
//        Port savedExportPort = portRepository.saveAndFlush(exportPort);
//        Port savedImportPort = portRepository.saveAndFlush(importPort);
//
//        // CARGO
//        // BoxSizeDto 객체 생성
//        CargoRequest.BoxSizeDto boxSizeDto = CargoRequest.BoxSizeDto.builder()
//            .width(new BigDecimal("50.0"))
//            .height(new BigDecimal("30.0"))
//            .depth(new BigDecimal("20.0"))
//            .build();
//
//        // CargoInfoDto 객체 생성
//        CargoRequest.CargoInfoDto cargoInfoDto = CargoRequest.CargoInfoDto.builder()
//            .productName("Electronics")
//            .hsCode("1234.56")
//            .incoterms("FOB")
//            .weight(new BigDecimal("15.5"))
//            .value(new BigDecimal("2000.0"))
//            .quantity(10)
//            .boxSize(boxSizeDto)
//            .build();
//
//        // CargoRequest 객체 생성
//        CargoRequest cargoRequest = CargoRequest.builder()
//            .exportPortId(savedExportPort.getId()) // 예시 포트 ID
//            .importPortId(savedImportPort.getId()) // 예시 포트 ID
//            .additionalInstructions("Handle with care.")
//            .friendlyDescription("Electronics shipment")
//            .insuranceRequired(true)
//            .cargoInfo(cargoInfoDto)
//            .build();
//
////        cargoService.createCargo(joinedUser.getId(), cargoRequest);
//        cargoService.createCargo(1L, cargoRequest);
//    }
}