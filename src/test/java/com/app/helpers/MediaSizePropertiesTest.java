package com.app.helpers;

import com.app.modules.media.enums.MediaContent;
import com.app.modules.media.enums.MediaSize;
import com.app.modules.media.properties.MediaSizeProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class MediaSizePropertiesTest {

    @Autowired
    private MediaSizeProperties mediaSizeProperties;

    @Test
    void testConfigLoaded() {
        assert mediaSizeProperties
                .getTypes().get(MediaContent.IMAGE)
                .getSizes().get(MediaSize.THUMBNAIL)
                .getWidth() == 150;
    }
}