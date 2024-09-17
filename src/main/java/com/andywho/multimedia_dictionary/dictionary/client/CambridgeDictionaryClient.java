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



//    private List<DictionaryInfo> parseDictionaryInfoFromHtml(String entryContent) {
//        Document doc = Jsoup.parse(entryContent);
//        List<DictionaryInfo> dictionaryInfoList = new ArrayList<>();
//
//        Elements posBlocks = doc.select("span.pos-block");
//
//        for (Element posBlock : posBlocks) {
//            DictionaryInfo dictionaryInfo = new DictionaryInfo();
//
//            // in here do we need to go to more specific elements inside the posblock? for example to to the header then info..
//            String pos = posBlock.select("span.pos").text();  // Extract part of speech
//            dictionaryInfo.setPos(pos);
//
//            // same question as above
//            String pronunciation = posBlock.select("span.pron").text();  // Extract pronunciation
//            dictionaryInfo.setPronunciation(pronunciation);
//
//            // let's ignore this part for now
//            Elements audioElements = posBlock.select("audio source[type=audio/mpeg]");
//            if (!audioElements.isEmpty()) {
//                String audioUrl = audioElements.first().attr("src");  // Get the first audio URL
//                // You can store this audio URL in the DictionaryInfo if required (e.g., setAudioUrl method)
//            }
//
//
//            Elements senseEntries = posBlock.select("section.senseEntry");
//
//            StringBuilder definitions = new StringBuilder();
//            List<String> sentences = new ArrayList<>();
//
//            for (Element senseEntry : senseEntries) {
//                // each of these element could be a def block? then i think def is 2 levels inside? do we need to go in step by step?
//                String definition = senseEntry.select("span.def").text();
//                if (!definition.isEmpty()) {
//                    definitions.append(definition).append(" ");
//                }
//
//                // same in here?
//                Elements exampleElements = senseEntry.select("span.examp span.eg");
//                for (Element example : exampleElements) {
//                    String exampleSentence = example.text();
//                    if (!exampleSentence.isEmpty()) {
//                        sentences.add(exampleSentence);  // Add example sentence to the list
//                    }
//                }
//            }
//
//            // Set the concatenated definitions and sentences into the DictionaryInfo object
//            dictionaryInfo.setDefinition(definitions.toString().trim());  // Set the combined definitions
//            dictionaryInfo.setSentences(sentences);  // Set the list of example sentences
//
//            // Add this DictionaryInfo object to the list
//            dictionaryInfoList.add(dictionaryInfo);
//        }
//
//        // Return the list of DictionaryInfo objects
//        return dictionaryInfoList;
//    }

    private List<DictionaryInfo> parseDictionaryInfoFromHtml(String entryContent) {
        // Parse the HTML content using Jsoup
        Document doc = Jsoup.parse(entryContent);

        // Initialize a list to hold multiple DictionaryInfo objects
        List<DictionaryInfo> dictionaryInfoList = new ArrayList<>();

        // Extract different pos-blocks (each represents a part of speech) from the HTML
        Elements posBlocks = doc.select("span.pos-block"); // adjust selector as needed

        for (Element posBlock : posBlocks) {
            DictionaryInfo dictionaryInfo = new DictionaryInfo();

            // Extract part of speech (pos) from the pos-block header
            String pos = posBlock.select("span.pos").text();  // Extract part of speech
            dictionaryInfo.setPos(pos);

            // Extract pronunciation (IPA) from the pos-block header
            String pronunciation = posBlock.select("span.pron").text();  // Extract pronunciation
            dictionaryInfo.setPronunciation(pronunciation);

            // Extract audio pronunciation URLs (if available)
            Elements audioElements = posBlock.select("audio source[type=audio/mpeg]");
            if (!audioElements.isEmpty()) {
                String audioUrl = audioElements.first().attr("src");  // Get the first audio URL
                // You can store this audio URL in the DictionaryInfo if required (e.g., setAudioUrl method)
            }

            // Extract different sense entries (each contains a definition, translation, and examples)
            Elements senseEntries = posBlock.select("section.senseEntry");

            List<String> definitions = new ArrayList<>();  // To hold multiple definitions with translations
            List<String> sentences = new ArrayList<>();    // To hold sentences with translations

            for (Element senseEntry : senseEntries) {
                // Loop through each definition span in this senseEntry
                Elements defElements = senseEntry.select("span.def");
                for (Element defElement : defElements) {
                    String definition = defElement.text();

                    // Check if a translation is present and append it to the definition
                    String translation = defElement.siblingElements().select("span.trans").text();
                    if (!translation.isEmpty()) {
                        definition = definition + " (" + translation + ")";  // Append the translation in parentheses
                    }

                    if (!definition.isEmpty()) {
                        definitions.add(definition);  // Add the definition (with translation) to the list
                    }
                }

                // Extract example sentences
                Elements exampleElements = senseEntry.select("span.examp span.eg");
                for (Element example : exampleElements) {
                    String exampleSentence = example.text();

                    // Check if a translation is present and append it to the example sentence
                    String sentenceTranslation = example.siblingElements().select("span.trans").text();
                    if (!sentenceTranslation.isEmpty()) {
                        exampleSentence = exampleSentence + " (" + sentenceTranslation + ")";  // Append translation
                    }

                    if (!exampleSentence.isEmpty()) {
                        sentences.add(exampleSentence);  // Add the example sentence (with translation)
                    }
                }
            }

            // Set the list of definitions and sentences into the DictionaryInfo object
            dictionaryInfo.setDefinition(String.join("; ", definitions));  // Join definitions with "; " separator
            dictionaryInfo.setSentences(sentences);  // Set the list of example sentences

            // Add this DictionaryInfo object to the list
            dictionaryInfoList.add(dictionaryInfo);
        }

        // Return the list of DictionaryInfo objects
        return dictionaryInfoList;
    }






}

