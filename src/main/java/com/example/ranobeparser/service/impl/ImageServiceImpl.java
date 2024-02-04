package com.example.ranobeparser.service.impl;

import com.example.ranobeparser.service.ImageService;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import org.springframework.stereotype.Service;

@Service
public class ImageServiceImpl implements ImageService {
    public String toBase64(String imgUrl) throws IOException {
        BufferedImage image = downloadImage(imgUrl);
        BufferedImage compressedImage = compressImage(image, imgUrl, 0.3f);
        
        return encode(compressedImage);
    }

    public BufferedImage downloadImage(String imgUrl) throws IOException {
        return ImageIO.read(new URL(imgUrl));
    }

    private BufferedImage compressImage(
            BufferedImage image,
            String url,
            float compressionRatio
    ) throws IOException {
        Iterator<ImageWriter> writers =
                ImageIO.getImageWritersByFormatName(getFormatName(url));
        ImageWriter writer = writers.next();

        ImageWriteParam params = writer.getDefaultWriteParam();
        params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        params.setCompressionQuality(compressionRatio);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageOutputStream imageOutputStream =
                ImageIO.createImageOutputStream(byteArrayOutputStream);
        writer.setOutput(imageOutputStream);

        writer.write(null, new IIOImage(image, null, null), params);

        imageOutputStream.close();
        writer.dispose();

        byte[] compressedImage = byteArrayOutputStream.toByteArray();

        return ImageIO.read(new ByteArrayInputStream(compressedImage));
    }

    private String encode(BufferedImage image) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", byteArrayOutputStream);

        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    public String getFormatName(String url) throws IOException {
        String[] splittedUrl = url.split("\\.");
        return splittedUrl[splittedUrl.length - 1];
    }
}
