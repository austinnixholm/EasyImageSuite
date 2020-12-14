package com.eis.models;

import lombok.Getter;

import java.awt.image.BufferedImage;

public class ResourceImage {
    @Getter
    public String imageSubPath;
    @Getter
    public BufferedImage image;
    public ResourceImage(String imageSubPath, BufferedImage image) {
        this.imageSubPath = imageSubPath;
        this.image = image;
    }
}
