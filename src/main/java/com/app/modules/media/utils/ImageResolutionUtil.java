package com.app.modules.media.utils;

import com.app.modules.media.metadata.Dimension;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

@Component
@Slf4j
public class ImageResolutionUtil {
    public static Dimension getImageDimension(InputStream inputStream) {
        try {
            BufferedImage image = ImageIO.read(inputStream);
            if (image != null) {
                return new Dimension(image.getWidth(), image.getHeight());
            }
        } catch (Exception e) {
            log.error("failed get image dimension cause {}", e.getMessage());
        }
        return new Dimension(0, 0);
    }
}
