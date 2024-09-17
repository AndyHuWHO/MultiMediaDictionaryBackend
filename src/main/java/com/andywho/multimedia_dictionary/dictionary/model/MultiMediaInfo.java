package com.andywho.multimedia_dictionary.dictionary.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class MultiMediaInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String link;
    private String uploader;
    //private String date_created;
    private LocalDateTime date_created;
    private int likes;
    private int collects;

    @ManyToMany
    private List<WordEntry> wordEntries;

    @PrePersist
    protected void onCreate() {
        date_created = LocalDateTime.now();
    }
}
