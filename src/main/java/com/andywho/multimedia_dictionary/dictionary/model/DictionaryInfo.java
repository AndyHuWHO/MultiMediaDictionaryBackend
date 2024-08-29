package com.andywho.multimedia_dictionary.dictionary.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DictionaryInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sourceDict = "Cambridge";
    private String pos = "";
    private String pronunciation = "";
    private String definition = "";
    private String translation = "";
    @ElementCollection
    private List<String> expressions = new ArrayList<String>();
    @ElementCollection
    private List<String> sentences = new ArrayList<String>();


    @ManyToOne
    private WordEntry wordEntry;

}
