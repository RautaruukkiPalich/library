package com.app.modules.media.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MediaProcessingScheduler {
    @Scheduled(fixedDelay = 5000, initialDelay = 10000)
    protected void convertMediaFiles() {
        System.out.println("convert files");
    }
}
