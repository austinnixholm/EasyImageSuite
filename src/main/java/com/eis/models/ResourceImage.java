package com.eis.models;

import lombok.Getter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * An object containing a {@link BufferedImage} object associated with this resource image,
 * as well as a sub-path to that image, from the original folder it was imported from.
 */
public class ResourceImage {
    @Getter
    public String imageSubPath;
    @Getter
    public BufferedImage image;

    /**
     * Constructor passing the sub path and the {@link BufferedImage} object associated with
     * this resource image.
     *
     * @param imageSubPath the image sub path
     * @param image        the image
     */
    public ResourceImage(String imageSubPath, BufferedImage image) {
        this.imageSubPath = imageSubPath;
        this.image = image;
    }
}
