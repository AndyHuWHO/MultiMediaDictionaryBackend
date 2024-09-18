package com.andywho.multimedia_dictionary.dictionary.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pronunciation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String dialect;    // e.g., "British", "American"
    private String ipa;        // IPA notation
    private String audioLink;  // Audio link for pronunciation

    @ManyToOne
    private DictionaryInfo dictionaryInfo;
}

