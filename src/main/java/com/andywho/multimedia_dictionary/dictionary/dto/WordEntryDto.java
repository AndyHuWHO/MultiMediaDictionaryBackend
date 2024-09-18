package com.andywho.multimedia_dictionary.dictionary.dto;

import com.andywho.multimedia_dictionary.dictionary.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WordEntryDto extends ResponseDto {
    private String word;
    private String language;
    private List<DictionaryInfoDto> dictionaryInfoList;

}
