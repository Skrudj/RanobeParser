package com.example.ranobeparser.service.impl;

import com.example.ranobeparser.model.Book;
import com.example.ranobeparser.model.Chapter;
import com.example.ranobeparser.model.Volume;
import com.example.ranobeparser.service.FileService;
import com.example.ranobeparser.service.ImageService;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Service
public class FileServiceImpl implements FileService {
    private static final int URL_INDEX = 0;
    private static final int IMAGE_ID_INDEX = 1;

    private ImageService imageService;
    private final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    private final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    private final TransformerFactory transformerFactory = TransformerFactory.newInstance();
    private final Transformer transformer = transformerFactory.newTransformer();

    public FileServiceImpl() throws
            ParserConfigurationException,
            TransformerConfigurationException {
    }

    @Autowired
    public void setImageService(ImageService imageService) {
        this.imageService = imageService;
    }

    public void write(String fileName, Book book) throws TransformerException {
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        Document document = documentBuilder.newDocument();

        Element root = document.createElement("FictionBook");
        addServiceTags(root, document, book);

        root.setAttribute("xmlns", "http://www.gribuser.ru/xml/fictionbook/2.0");
        root.setAttribute("xmlns:l", "http://www.w3.org/1999/xlink");
        document.appendChild(root);

        fillContent(root, document, book);

        File bookFile = new File(fileName + ".fb2");
        DOMSource sourceFile = new DOMSource(document);
        StreamResult fileStream = new StreamResult(bookFile);

        transformer.transform(sourceFile, fileStream);
    }

    private static void addServiceTags(Element root, Document document, Book book) {
        Element description = document.createElement("description");
        root.appendChild(description);

        Element titleInfo = document.createElement("title-info");
        description.appendChild(titleInfo);

        Element author = document.createElement("author");
        titleInfo.appendChild(author);

        Element firstName = document.createElement("first-name");
        author.appendChild(firstName);

        Element lastName = document.createElement("last-name");
        author.appendChild(lastName);

        Element bookTitle = document.createElement("book-title");
        titleInfo.appendChild(bookTitle);

        firstName.setTextContent(book.getDescription().getAuthor().split(" ")[0]);
        lastName.setTextContent(book.getDescription().getAuthor().split(" ")[1]);
        bookTitle.setTextContent(book.getDescription().getBookTitle());
    }

    private void fillContent(Element root, Document document, Book book) {
        Element body = document.createElement("body");
        Element title = document.createElement("title");

        root.appendChild(body);
        body.appendChild(title);
        title.setTextContent(book.getDescription().getBookTitle());

        for (Volume volume: book.getVolumeList()) {
            Element volumeSection = document.createElement("section");

            Element volumeTitle = document.createElement("title");
            volumeSection.appendChild(volumeTitle);
            Element volumeTitleTag = document.createElement("p");
            volumeTitle.appendChild(volumeTitleTag);
            volumeTitleTag.setTextContent(volume.getTitle());

            for (Chapter chapter: volume.getChapterList()) {
                Element chapterSection = document.createElement("section");

                Element chapterTitle = document.createElement("title");
                chapterSection.appendChild(chapterTitle);
                Element chapterTitleTag = document.createElement("p");
                chapterTitle.appendChild(chapterTitleTag);
                chapterTitleTag.setTextContent(chapter.getTitle());

                chapter.getContent()
                        .forEach(content -> {
                            Element paragraphTag = document.createElement("p");
                            switch (content.tagName()) {
                                case "p":
                                    paragraphTag.setTextContent(content.text());
                                    break;
                                case "div":
                                    try {
                                        String[] imgData = getDataFromElement(content);
                                        System.out.println(imgData[URL_INDEX]);
                                        if (imgData[URL_INDEX].endsWith(".gif")) {
                                            break;
                                        }
                                        Element binary = document.createElement("binary");
                                        setBinaryTags(binary, imgData[IMAGE_ID_INDEX]);
                                        binary.setTextContent(imageService
                                                .toBase64(imgData[URL_INDEX]));

                                        Element image = document.createElement("image");
                                        image.setAttribute("l:href", "#" + imgData[IMAGE_ID_INDEX]);

                                        paragraphTag.appendChild(binary);
                                        paragraphTag.appendChild(image);
                                        break;
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                default: break;
                            }
                            chapterSection.appendChild(paragraphTag);
                        });

                volumeSection.appendChild(chapterSection);
                body.appendChild(volumeSection);
            }
        }
    }

    private static String[] getDataFromElement(org.jsoup.nodes.Element div) {
        String url = div.child(0).attr("data-src");
        String[] parts = url.split("/");
        String id = parts[parts.length - 1];
        id = id.substring(0, id.length() - 4);

        return new String[]{url, id};
    }

    private static void setBinaryTags(Element binary, String id) {
        binary.setAttribute("id", id);
        binary.setAttribute("content-type", "image/jpeg");
        binary.setAttribute("encoding", "base64");
    }
}
