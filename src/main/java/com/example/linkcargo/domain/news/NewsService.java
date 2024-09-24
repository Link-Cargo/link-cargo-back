package com.example.linkcargo.domain.news;

import com.example.linkcargo.global.response.code.resultCode.ErrorStatus;
import com.example.linkcargo.global.response.exception.handler.GeneralHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsService {

    private final NewsRepository newsRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    public void createNewsAboutInterest() {
        // 기본 관심사
        List<String> searchKeywords = Arrays.asList("환율","상하이","운임지수");

        String API_URL = "https://lrib48jimi.execute-api.ap-northeast-2.amazonaws.com/default/test";

        for (String keyword: searchKeywords) {
            String urlWithParams = API_URL + "?search=" + keyword;
            try {
                ResponseEntity<String> responseEntity = restTemplate.getForEntity(urlWithParams, String.class);
                String jsonResponse = responseEntity.getBody();

                Map<String, String> response = objectMapper.readValue(jsonResponse, Map.class);


                System.out.println(response);
                String query = response.get("query");
                String summary = response.get("summary");

                if (query != null && summary != null) {
                    String title = query + " 뉴스 " + LocalDate.now();

                    News news = News.builder()
                        .title(title)
                        .category(query)
                        .content(summary)
                        .build();

                    newsRepository.save(news);
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
                throw new GeneralHandler(ErrorStatus.EXTERNAL_API_ERROR);
            }
        }
    }
}
