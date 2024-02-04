package com.example.ranobeparser;

import com.example.ranobeparser.model.Book;
import com.example.ranobeparser.model.Description;
import com.example.ranobeparser.service.BookParser;
import com.example.ranobeparser.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RanobeParserApplication {
    @Value("${first.chapter.url}")
    private static String URL;
    @Value("${book.name}")
    private static String BOOK_NAME;
    @Value("${book.author}")
    private static String AUTHOR;

    private static BookParser bookParser;
    private static FileService fileService;

    public RanobeParserApplication(BookParser bookParser, FileService fileService) {
        RanobeParserApplication.bookParser = bookParser;
        RanobeParserApplication.fileService = fileService;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(RanobeParserApplication.class, args);
        Description description = new Description(BOOK_NAME, AUTHOR);
        Book book = new Book(URL, description);
        book = bookParser.parseBook(book);
        fileService.write(BOOK_NAME, book);
    }
}
