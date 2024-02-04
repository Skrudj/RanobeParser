package com.example.ranobeparser.service.impl;

import com.example.ranobeparser.service.MyHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

@Service
public class MyHttpClientImpl implements MyHttpClient {
    public Document fetch(String url) throws Exception {
        return Jsoup.connect(url).get();
    }
}
