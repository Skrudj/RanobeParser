package com.example.ranobeparser.service;

import com.example.ranobeparser.model.Book;

public interface BookParser {
    Book parseBook(Book book) throws Exception;
}
