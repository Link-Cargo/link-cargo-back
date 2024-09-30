package com.example.linkcargo.domain.prediction;

import com.example.linkcargo.domain.prediction.dto.request.PredictionCreateRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PredictionService {

    private final PredictionRepository predictionRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    public void createPrediction(PredictionCreateRequest request) {
        String url = "http://43.202.227.122:8000/predict/";

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> jsonBodyMap = new HashMap<>();

        jsonBodyMap.put("fairway", "한중");
        jsonBodyMap.put("ship_volume", request.shipVolume());
        jsonBodyMap.put("ship_delivery_volume", request.shipDeliveryVolume());
        jsonBodyMap.put("ship_order_volume", request.shipOrderVolume());
        jsonBodyMap.put("idle_ship_volume", request.idleShipVolume());
        jsonBodyMap.put("export_volume_from_china", request.exportVolumeFromChina());
        jsonBodyMap.put("month", request.month());

        String jsonBody;
        try {
            jsonBody = objectMapper.writeValueAsString(jsonBodyMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 변환 오류", e);
        }

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        try {
            // API 호출
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
            );

            // 응답 처리
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                JsonNode predictionNode = rootNode.get("prediction");

                if (predictionNode != null && predictionNode.isArray()
                    && !predictionNode.isEmpty()) {
                    Double predictionValue = Double.valueOf(predictionNode.get(0).asText());
                    Integer predictionIndex = predictionValue.intValue();

                    Prediction prediction = Prediction.builder()
                        .freightCostIndex(String.valueOf(predictionIndex))
                        .year(request.year())
                        .month(request.month())
                        .build();

                    predictionRepository.save(prediction);
                }
            }
        } catch (Exception e) {
        }
    }

    }
