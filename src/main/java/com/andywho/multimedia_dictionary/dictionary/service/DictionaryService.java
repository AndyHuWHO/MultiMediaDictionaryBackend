package com.andywho.multimedia_dictionary.dictionary.service;

import com.andywho.multimedia_dictionary.dictionary.model.NoteInfo;
import com.andywho.multimedia_dictionary.dictionary.model.WordEntry;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


public interface DictionaryService {
    /**
     * Finds and retrieves the {@link WordEntry} associated with the given word.
     *
     * @param word the word to search for in the dictionary
     * @return the {@link WordEntry} corresponding to the provided word,
     *         or {@code null} if the word is not found
     */
    Mono<WordEntry> lookupWord(String word);

}
