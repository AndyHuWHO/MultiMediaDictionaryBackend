package com.andywho.multimedia_dictionary.dictionary.controller;

import com.andywho.multimedia_dictionary.dictionary.dto.EntityDtoMapper;
import com.andywho.multimedia_dictionary.dictionary.dto.ErrorResponse;
import com.andywho.multimedia_dictionary.dictionary.dto.ResponseDto;
import com.andywho.multimedia_dictionary.dictionary.dto.WordEntryDto;
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
    public Mono<ResponseEntity<ResponseDto>> searchWord(@PathVariable String word) {
        return dictionaryService.lookupWord(word.toLowerCase())
                .map(wordEntry -> {
                    WordEntryDto wordEntryDto = entityDtoMapper.toWordEntryDto(wordEntry);
                    return new ResponseEntity<ResponseDto>(wordEntryDto, HttpStatus.OK);
                })
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND))
                .onErrorResume(e -> {
                    ErrorResponse errorResponse =
                            new ErrorResponse("Error",
                                                e.getMessage(),
                                                e.getMessage().contains("404") ?
                                                        "Not Found" : "Internal Server Error");
                    HttpStatus status = e.getMessage().contains("404") ?
                            HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR;
                    return Mono.just(new ResponseEntity<ResponseDto>(errorResponse, status));
                });
    }

}
