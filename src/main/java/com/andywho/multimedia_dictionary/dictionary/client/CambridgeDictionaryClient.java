package com.andywho.multimedia_dictionary.dictionary.client;

import com.andywho.multimedia_dictionary.dictionary.model.DictionaryInfo;
import com.andywho.multimedia_dictionary.dictionary.model.Pronunciation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
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
//    private final RestTemplate restTemplate;
    private WebClient webClient;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    @Value("${cambridge.api.url}")
    private String apiUrl;

    @Value("${cambridge.api.key}")
    private String accessKey;

    public CambridgeDictionaryClient(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
//        this.restTemplate = restTemplate;
        this.webClientBuilder = webClientBuilder;
        this.objectMapper = objectMapper;
    }


    @PostConstruct
    public void init() {
        System.out.println("API URL after injection: " + apiUrl);
        this.webClient = webClientBuilder.baseUrl(apiUrl).build();
    }

    public Mono<List<DictionaryInfo>> fetchWordEntry(String word) {
        return webClient
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

        // Extract different pos-blocks (each represents one potential dictionary info)
        Elements posBlocks = doc.select("span.pos-block"); // adjust selector as needed

        for (Element posBlock : posBlocks) {
            DictionaryInfo dictionaryInfo = new DictionaryInfo();

            // Extract part of speech (pos) from the pos-block header
            String pos = posBlock.select("span.pos").text();
            dictionaryInfo.setPos(pos);

            // Initialize the list of pronunciations
            List<Pronunciation> pronunciations = new ArrayList<>();
            Element pronunciationContainer = posBlock.selectFirst("header span.info");

            // Check if the container exists
            if (pronunciationContainer != null) {
                // Loop through each child 'span.info' block that holds individual pronunciation data
                Elements childPronunciationBlocks = pronunciationContainer.children();
                for (Element pronunciationElement : childPronunciationBlocks) {
                    Pronunciation pronunciation = new Pronunciation();
                    // Determine the dialect based on the 'alt' attribute of the image
                    Element imageElement = pronunciationElement.selectFirst("a.playback img");
                    String dialect = "";
                    if (imageElement != null) {
                        String altText = imageElement.attr("alt");
                        if (altText.contains("British")) {
                            dialect = "British";
                        } else if (altText.contains("American")) {
                            dialect = "American";
                        }
                    }
                    pronunciation.setDialect(dialect);

//                    // Extract the IPA pronunciation
//                    Element ipaElement = pronunciationElement.selectFirst("span.ipa");
//                    if (ipaElement != null) {
//                        String ipa = ipaElement.text();
//                        ipa = ipa.replaceAll("<sup>(.*?)</sup>", "($1)");  // Replace any <sup> with parentheses
//                        pronunciation.setIpa(ipa);
//                    }

                    // Extract the IPA pronunciation as HTML (to keep the <sup> elements)
                    Element ipaElement = pronunciationElement.selectFirst("span.ipa");
                    if (ipaElement != null) {
                        String ipaHtml = ipaElement.html();
                        // Replace <sup> and </sup> tags with parentheses
                        ipaHtml = ipaHtml.replaceAll("<sup>", "(")
                                .replaceAll("</sup>", ")");

                        // Set the cleaned IPA value
                        pronunciation.setIpa(ipaHtml);
                    }


                    // Extract the audio link (if available)
                    Element audioElement = pronunciationElement.selectFirst("audio source[type=audio/mpeg]");
                    if (audioElement != null) {
                        String audioLink = audioElement.attr("src");
                        pronunciation.setAudioLink(audioLink);
                    }

                    // Only add the pronunciation if it has a valid dialect and IPA
                    if (!dialect.isEmpty() && ipaElement != null) {
                        pronunciations.add(pronunciation);
                    }
                }
            }

            // Set the list of Pronunciation objects in DictionaryInfo
            dictionaryInfo.setPronunciations(pronunciations);


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
                        definition = definition + " (" + translation + ")";
                    }

                    if (!definition.isEmpty()) {
                        definitions.add(definition);
                    }
                }

                // Extract example sentences
                Elements exampleElements = senseEntry.select("span.examp span.eg");
                for (Element example : exampleElements) {
                    String exampleSentence = example.text();

                    // Check if a translation is present for the example sentence
                    String sentenceTranslation = example.siblingElements().select("span.trans").text();
                    if (!sentenceTranslation.isEmpty()) {
                        exampleSentence = exampleSentence + " (" + sentenceTranslation + ")";  // Append translation
                    }

                    if (!exampleSentence.isEmpty()) {
                        sentences.add(exampleSentence);  // Add example sentence (with translation) to the list
                    }
                }
            }

            // Set the list of definitions and sentences into the DictionaryInfo object
            dictionaryInfo.setDefinition(definitions);  // Store all definitions with translations appended
            dictionaryInfo.setSentences(sentences);  // Store all sentences with translations appended

            // Add this DictionaryInfo object to the list
            dictionaryInfoList.add(dictionaryInfo);
        }

        // Return the list of DictionaryInfo objects
        return dictionaryInfoList;
    }

}

