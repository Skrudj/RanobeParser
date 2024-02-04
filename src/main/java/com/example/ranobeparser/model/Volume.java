package com.example.ranobeparser.model;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RequiredArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class Volume {
    @NonNull
    private String title;
    private List<Chapter> chapterList;
}
