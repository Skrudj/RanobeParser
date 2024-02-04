package com.example.ranobeparser.service;

import org.jsoup.nodes.Document;

public interface MyHttpClient {
    public Document fetch(String url) throws Exception;
}
