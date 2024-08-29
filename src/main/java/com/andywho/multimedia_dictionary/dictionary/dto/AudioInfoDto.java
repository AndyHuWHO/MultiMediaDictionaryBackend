package com.andywho.multimedia_dictionary.dictionary.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AudioInfoDto extends MultiMediaInfoDto{
    public AudioInfoDto(List<String> relatedWords,
                        String link,
                        String uploader,
                        LocalDateTime dateCreated,
                        int likes,
                        int collects) {
        super(relatedWords, link, uploader, dateCreated, likes, collects);
    }
}
