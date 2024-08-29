package com.andywho.multimedia_dictionary.dictionary.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class MultiMediaInfoDto {
    private List<String> relatedWords;
    private String link;
    private String uploader;
    private LocalDateTime dateCreated;
    private int likes;
    private int collects;
}
