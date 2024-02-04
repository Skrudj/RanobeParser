package com.example.ranobeparser.service.impl;

import com.example.ranobeparser.model.Book;
import com.example.ranobeparser.model.Chapter;
import com.example.ranobeparser.model.Volume;
import com.example.ranobeparser.service.BookParser;
import com.example.ranobeparser.service.MyHttpClient;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookParserImpl implements BookParser {
    private MyHttpClient myHttpClient;

    @Autowired
    public void setMyHttpClient(MyHttpClient myHttpClient) {
        this.myHttpClient = myHttpClient;
    }

    public Book parseBook(Book book) throws Exception {
        List<Volume> volumes = new LinkedList<>();
        String currentUrl = book.getFirstParagraphLink();
        Document document = myHttpClient.fetch(currentUrl);

        while (!Objects.equals(getNextUrl(document), "#")) {
            Volume volume = new Volume("Том " + getVolumeFromUrl(currentUrl));
            List<Chapter> chapters = new ArrayList<>();
            boolean flag = true;

            while (flag) {
                document = myHttpClient.fetch(currentUrl);
                Chapter chapter = new Chapter(getChapterTitle(document));
                chapter.setContent(getParagraphContent(document));
                chapters.add(chapter);

                if (getVolumeFromUrl(currentUrl) != getVolumeFromUrl(getNextUrl(document))) {
                    flag = false;
                }

                currentUrl = getNextUrl(document);

                System.out.println(getChapterTitle(document));
            }
            volume.setChapterList(chapters);
            volumes.add(volume);
            System.out.println(getVolumeFromUrl(currentUrl));
        }

        book.setVolumeList(volumes);
        return book;
    }

    private static String getChapterTitle(Document document) {
        Elements meta = document.select("meta");
        String lastTag = meta.last().toString();
        return lastTag.substring(95, lastTag.indexOf('.'));
    }

    public static int getVolumeFromUrl(String url) {
        String[] splitUrl = url.split("/");

        if (splitUrl.length == 1) {
            return -1;
        }

        return Integer.parseInt(splitUrl[splitUrl.length - 2].substring(1));
    }

    private static String getNextUrl(Document document) {
        return document.select("a.reader-header-action").last().attributes().get("href");
    }

    public static Elements getParagraphContent(Document document) {
        return document.select("div.reader-container > div, div.reader-container > p");
    }
}
