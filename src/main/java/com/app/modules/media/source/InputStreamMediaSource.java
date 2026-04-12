package com.app.modules.media.source;

import lombok.RequiredArgsConstructor;

import java.io.InputStream;

@RequiredArgsConstructor
public class InputStreamMediaSource implements MediaSource {
    private final InputStream inputStream;
    private final String contentType;
    private final String filename;
    private final Long size;

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public String getOriginalFilename() {
        return filename;
    }

    @Override
    public Long getSize() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return inputStream == null || size == null || size == 0L;
    }
}
