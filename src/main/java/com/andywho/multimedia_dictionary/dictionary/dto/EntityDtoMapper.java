package com.andywho.multimedia_dictionary.dictionary.dto;

import com.andywho.multimedia_dictionary.dictionary.model.*;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class EntityDtoMapper {
    public WordEntryDto toWordEntryDto(WordEntry wordEntry) {
        return new WordEntryDto(
                wordEntry.getWord(),
                wordEntry.getLanguage(),
                wordEntry.getDictionaryInfoList().stream()
                        .map(this::toDictionaryInfoDto)
                        .collect(Collectors.toList())
        );
    }

    public DictionaryInfoDto toDictionaryInfoDto(DictionaryInfo dictionaryInfo) {
        return new DictionaryInfoDto(
                dictionaryInfo.getSourceDict(),
                dictionaryInfo.getPos(),
                dictionaryInfo.getPronunciation(),
                dictionaryInfo.getDefinition(),
                dictionaryInfo.getTranslation(),
                dictionaryInfo.getExpressions(),
                dictionaryInfo.getSentences()
        );
    }

    public NoteInfoDto toNoteInfoDto(NoteInfo noteInfo) {
        return new NoteInfoDto(
                noteInfo.getUploader(),
                noteInfo.getDate_created(),
                noteInfo.getContent()
        );
    }

    public VideoInfoDto toVideoInfoDto(VideoInfo videoInfo) {
        return new VideoInfoDto(
                videoInfo.getWordEntries().stream()
                        .map(WordEntry::getWord)
                        .collect(Collectors.toList()),
                videoInfo.getLink(),
                videoInfo.getUploader(),
                videoInfo.getDate_created(),
                videoInfo.getLikes(),
                videoInfo.getCollects()
        );
    }

    public AudioInfoDto toAudioInfoDto(AudioInfo audioInfo) {
        return new AudioInfoDto(
                audioInfo.getWordEntries().stream()
                            .map(WordEntry::getWord)
                            .collect(Collectors.toList()),
                audioInfo.getLink(),
                audioInfo.getUploader(),
                audioInfo.getDate_created(),
                audioInfo.getLikes(),
                audioInfo.getCollects()
        );
    }


    public ImageInfoDto toPictureInfoDto(ImageInfo imageInfo) {
        return new ImageInfoDto(
                imageInfo.getWordEntries().stream()
                            .map(WordEntry::getWord)
                            .collect(Collectors.toList()),
                imageInfo.getLink(),
                imageInfo.getUploader(),
                imageInfo.getDate_created(),
                imageInfo.getLikes(),
                imageInfo.getCollects()
        );
    }

}
