package com.eis.models;

import lombok.Getter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResourceImage {
    @Getter
    public String imageSubPath;
    @Getter
    public BufferedImage image;

    public ResourceImage(String imageSubPath, byte[] imageBytes) {
        this.imageSubPath = imageSubPath;
        InputStream is = new ByteArrayInputStream(imageBytes);
        try {
            this.image = ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ResourceImage(String imageSubPath, BufferedImage image) {
        this.imageSubPath = imageSubPath;
        this.image = image;
    }
}
