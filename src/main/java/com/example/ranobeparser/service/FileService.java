package com.example.ranobeparser.service;

import com.example.ranobeparser.model.Book;
import javax.xml.transform.TransformerException;

public interface FileService {
    void write(String fileName, Book book) throws TransformerException;
}
