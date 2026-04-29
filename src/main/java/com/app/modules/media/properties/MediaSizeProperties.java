package com.app.modules.media.properties;

import com.app.modules.media.enums.MediaContent;
import com.app.modules.media.enums.MediaSize;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;


@Data
@Slf4j
@Component
@ConfigurationProperties(prefix = "media.types")
public class MediaSizeProperties {
    private Map<MediaContent, MediaConfig> types = new EnumMap<>(MediaContent.class);

    @PostConstruct
    protected void postConstruct() {
        types.put(MediaContent.IMAGE, image);
        types.put(MediaContent.VIDEO, video);

        validate();
        logConfig();
    }

    private void validate() throws IllegalArgumentException {
        types.forEach((type, cfg) -> {
            cfg.getSizes().forEach((size, params) -> {
                try {
                    params.validate();
                } catch (Exception e) {
                    log.error("{}:{} {}", type, size, e.getMessage());
                    throw e;
                }
            });
        });
    }

    private final MediaConfig image = new MediaConfig();
    private final MediaConfig video = new MediaConfig();

    @Data
    public static class MediaConfig {
        private Map<MediaSize, SizeConfig> sizes = new HashMap<>();
    }

    @Data
    public static class SizeConfig {
        @Min(0)
        @Max(3840)
        private int width = 0;

        @Min(0)
        @Max(2160)
        private int height = 0;

        @Min(1)
        @Max(100)
        private int quality = 100;

        private boolean keepAspectRatio = true;
        private boolean cropToSquare = false;

        private void validate() {
            if (height < 0 || height > 2160) {
                throw new IllegalArgumentException("height must be between 0 and 2160 but got %s".formatted(height));
            }
            if (width < 0 || width > 3840) {
                throw new IllegalArgumentException("width must be between 0 and 3840 but got %s".formatted(width));
            }
            if (quality < 1 || quality > 100) {
                throw new IllegalArgumentException("quality must be between 1 and 100 but got %s".formatted(quality));
            }
            if (cropToSquare && (width != height || width <= 0)) {
                throw new IllegalArgumentException("cropToSquare requires positive equal dimensions, but got %dx%d".formatted(width, height));
            }
            if (cropToSquare && keepAspectRatio) {
                throw new IllegalArgumentException("cannot have both cropToSquare=true AND keepAspectRatio=true");
            }
        }
    }

    protected void logConfig() {
        types.forEach((contentType, config) -> {
            log.info("ContentType: {}", contentType);
            log.info("  Sizes: {}", config.getSizes().keySet());

            config.getSizes().forEach((size, sizeConfig) -> {
                log.info("    {}: {}x{}, quality={}, cropToSquare={}, keepAspectRatio={}",
                        size,
                        sizeConfig.getWidth(),
                        sizeConfig.getHeight(),
                        sizeConfig.getQuality(),
                        sizeConfig.isCropToSquare(),
                        sizeConfig.isKeepAspectRatio()
                );
            });
        });
    }
}

