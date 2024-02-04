package com.example.ranobeparser.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jsoup.select.Elements;

@RequiredArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
public class Chapter {
    @NonNull
    private String title;
    private Elements content;

    @Override
    public String toString() {
        return "Chapter{"
                + "title='" + title + '\''
                + ", paragraphs=" + content + '\''
                + '}';
    }
}
