package com.example.ranobeparser.service;

import java.io.IOException;

public interface ImageService {
    String toBase64(String url) throws IOException;
}
