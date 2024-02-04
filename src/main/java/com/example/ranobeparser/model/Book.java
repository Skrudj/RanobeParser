package com.example.ranobeparser.model;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class Book {
    @NonNull
    private String firstParagraphLink;
    @NonNull
    private Description description;
    private List<Volume> volumeList;
}
