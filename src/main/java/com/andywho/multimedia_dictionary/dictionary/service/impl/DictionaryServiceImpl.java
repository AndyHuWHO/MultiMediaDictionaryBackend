package com.andywho.multimedia_dictionary.dictionary.service.impl;

import com.andywho.multimedia_dictionary.dictionary.client.CambridgeDictionaryClient;
import com.andywho.multimedia_dictionary.dictionary.model.DictionaryInfo;
import com.andywho.multimedia_dictionary.dictionary.model.NoteInfo;
import com.andywho.multimedia_dictionary.dictionary.model.WordEntry;
import com.andywho.multimedia_dictionary.dictionary.service.DictionaryService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class DictionaryServiceImpl implements DictionaryService {

    private final CambridgeDictionaryClient cambridgeDictionaryClient;

    public DictionaryServiceImpl(CambridgeDictionaryClient cambridgeDictionaryClient) {
        this.cambridgeDictionaryClient = cambridgeDictionaryClient;
    }

    @Override
    public Mono<WordEntry> lookupWord(String word) {
        return cambridgeDictionaryClient.fetchWordEntry(word)
                .flatMap(dictionaryInfoList -> {
                    if (dictionaryInfoList.isEmpty()) {
                        return Mono.empty();
                    }
                    WordEntry wordEntry = new WordEntry(word, dictionaryInfoList);
                    dictionaryInfoList.forEach(dictionaryInfo -> dictionaryInfo.setWordEntry(wordEntry));
                    return Mono.just(wordEntry);
                });
    }
}
