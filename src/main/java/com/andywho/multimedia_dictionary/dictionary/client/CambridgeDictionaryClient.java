package com.andywho.multimedia_dictionary.dictionary.client;

import com.andywho.multimedia_dictionary.dictionary.model.DictionaryInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

@Component
public class CambridgeDictionaryClient {

//    private final WebClient webClient;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${cambridge.api.url}")
    private String apiUrl;

    @Value("${cambridge.api.key}")
    private String accessKey;

    public CambridgeDictionaryClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
//        this.webClient = webClientBuilder.baseUrl(apiUrl).build();
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public List<DictionaryInfo> fetchWordEntry(String word) {
        try {
            String url = String.format("%s/%s/?format=json", apiUrl, word);
            HttpHeaders headers = new HttpHeaders();
            headers.set("accessKey", accessKey);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            String responseBody = response.getBody();

            // Parse the JSON response
            JsonNode rootNode = objectMapper.readTree(responseBody);
            System.out.println("rootNode is: " + rootNode);

            String entryContent = rootNode.path("entryContent").asText();

            DictionaryInfo dictionaryInfo = new DictionaryInfo();
            dictionaryInfo.setDefinition(entryContent);

            return Collections.singletonList(dictionaryInfo);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch or parse JSON response", e);
        }
    }

}

