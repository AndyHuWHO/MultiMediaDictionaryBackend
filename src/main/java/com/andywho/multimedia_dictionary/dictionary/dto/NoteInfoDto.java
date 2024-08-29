package com.andywho.multimedia_dictionary.dictionary.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoteInfoDto {
    private String uploader;
    private String date_created;
    private String content;
}
