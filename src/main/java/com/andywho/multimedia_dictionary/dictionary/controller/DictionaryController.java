package com.andywho.multimedia_dictionary.dictionary.controller;

import com.andywho.multimedia_dictionary.dictionary.dto.EntityDtoMapper;
import com.andywho.multimedia_dictionary.dictionary.dto.WordEntryDto;
import com.andywho.multimedia_dictionary.dictionary.model.WordEntry;
import com.andywho.multimedia_dictionary.dictionary.service.DictionaryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/dictionary")
public class DictionaryController {
    private final DictionaryService dictionaryService;
    private final EntityDtoMapper entityDtoMapper;

    public DictionaryController(DictionaryService dictionaryService, EntityDtoMapper entityDtoMapper) {
        this.dictionaryService = dictionaryService;
        this.entityDtoMapper = entityDtoMapper;
    }

    @GetMapping("/search/{word}")
    public ResponseEntity<WordEntryDto> searchWord(@PathVariable String word) {
        WordEntry wordEntry = dictionaryService.lookupWord(word);
        WordEntryDto wordEntryDto = entityDtoMapper.toWordEntryDto(wordEntry);
        return new ResponseEntity<>(wordEntryDto,HttpStatus.OK);
    }
}
