package com.andywho.multimedia_dictionary.dictionary.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String uploader;
    private String date_created;
    private String content;

    @ManyToOne
    private WordEntry wordEntry;
}
