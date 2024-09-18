package com.andywho.multimedia_dictionary.dictionary.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DictionaryInfoDto {
    private String sourceDict;
    private String pos ;
    private List<PronunciationDto> pronunciation ;
    private List<String> definition;
    private List<String> translation;
    private List<String> expressions;
    private List<String> sentences;
}
