package com.andywho.multimedia_dictionary.dictionary.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WordEntry {
    @Id
    private String word;

    private String language;

    @OneToMany(mappedBy = "wordEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DictionaryInfo> dictionaryInfoList;

    @ManyToMany
    private List<VideoInfo> videoInfoList = new ArrayList<>();

    @ManyToMany
    private List<ImageInfo> imageInfoList = new ArrayList<>();

    @ManyToMany
    private List<AudioInfo> audioInfoList = new ArrayList<>();

    @OneToMany
    private List<NoteInfo> noteInfo = new ArrayList<>();


    public WordEntry(String word, List<DictionaryInfo> dictionaryInfoList) {
        this.word = word;
        this.language = "English";
        this.dictionaryInfoList = dictionaryInfoList;
    }
}
