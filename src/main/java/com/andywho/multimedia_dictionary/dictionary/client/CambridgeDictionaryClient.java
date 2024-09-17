package com.andywho.multimedia_dictionary.dictionary.client;

import com.andywho.multimedia_dictionary.dictionary.model.DictionaryInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class CambridgeDictionaryClient {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${cambridge.api.url}")
    private String apiUrl;

    @Value("${cambridge.api.key}")
    private String accessKey;

    public CambridgeDictionaryClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

//    public Mono<List<DictionaryInfo>> fetchWordEntry(String word) {
//        return WebClient.create(apiUrl)
//                .get()
//                .uri(uriBuilder -> uriBuilder.path("/{word}/")
//                        .queryParam("format", "json")
//                        .build(word))
//                .header("accessKey", accessKey)
//                .retrieve()
//                .bodyToMono(String.class)
//                .flatMap(responseBody -> {
//                    try {
//                        JsonNode rootNode = objectMapper.readTree(responseBody);
//                        String entryContent = rootNode.path("entryContent").asText();
//                        DictionaryInfo dictionaryInfo = new DictionaryInfo();
//                        dictionaryInfo.setDefinition(entryContent);
//                        return Mono.just(Collections.singletonList(dictionaryInfo));
//
//                    } catch (Exception e) {
//                        // Log the error (optional)
//                        return Mono.error(new RuntimeException(e.getMessage()));
//                    }
//                })
//                .onErrorResume(e -> {
//                    System.out.println("Exception in dictionary client: " + e);
//                    return Mono.error(new RuntimeException(e.getMessage()));
//                });
//    }
public Mono<List<DictionaryInfo>> fetchWordEntry(String word) {
    return WebClient.create(apiUrl)
            .get()
            .uri(uriBuilder -> uriBuilder.path("/{word}/")
                    .queryParam("format", "json")
                    .build(word))
            .header("accessKey", accessKey)
            .retrieve()
            .bodyToMono(String.class)
            .flatMap(responseBody -> {
                try {
                    // Parse the JSON response to get the HTML content
                    JsonNode rootNode = objectMapper.readTree(responseBody);
                    String entryContent = rootNode.path("entryContent").asText();

                    // Call the helper method to parse and create a list of DictionaryInfo objects
                    List<DictionaryInfo> dictionaryInfoList = parseDictionaryInfoFromHtml(entryContent);

                    // Return the list wrapped in a Mono
                    return Mono.just(dictionaryInfoList);

                } catch (Exception e) {
                    // Handle and log the error
                    return Mono.error(new RuntimeException(e.getMessage()));
                }
            })
            .onErrorResume(e -> {
                System.out.println("Exception in dictionary client: " + e);
                return Mono.error(new RuntimeException(e.getMessage()));
            });
}



    private List<DictionaryInfo> parseDictionaryInfoFromHtml(String entryContent) {
        // Parse the HTML content using Jsoup
        Document doc = Jsoup.parse(entryContent);

        // Initialize a list to hold multiple DictionaryInfo objects
        List<DictionaryInfo> dictionaryInfoList = new ArrayList<>();

        // Extract different senses/meanings from the HTML
        Elements senseEntries = doc.select("section.senseEntry"); // adjust selector as needed
        for (Element senseEntry : senseEntries) {
            DictionaryInfo dictionaryInfo = new DictionaryInfo();

            // Extract part of speech (pos)
            // Example of extracting pos: "noun" etc.
            String pos = senseEntry.select("span.pos").text();  // Adjust selector based on your HTML structure
            dictionaryInfo.setPos(pos);

            // Extract pronunciation (can be extended to fetch audio URLs)
            // Ensure pronunciation is extracted correctly, check if it uses another HTML element.
            String pronunciation = senseEntry.select("span.pron").text();
            dictionaryInfo.setPronunciation(pronunciation);

            // Extract definition
            String definition = senseEntry.select("span.def").text();
            dictionaryInfo.setDefinition(definition);

            // Extract example sentences
            List<String> sentences = new ArrayList<>();
            Elements examples = senseEntry.select("span.examp span.eg");
            for (Element example : examples) {
                sentences.add(example.text());
            }
            dictionaryInfo.setSentences(sentences);

            // Add this DictionaryInfo to the list
            dictionaryInfoList.add(dictionaryInfo);
        }

        // Return the list of DictionaryInfo objects
        return dictionaryInfoList;
    }



}

