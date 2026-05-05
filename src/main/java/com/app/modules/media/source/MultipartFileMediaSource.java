package com.app.modules.media.source;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RequiredArgsConstructor
public class MultipartFileMediaSource implements MediaSource {
    private final MultipartFile file;

    @Override
    public InputStream getInputStream() throws IOException {
        return file.getInputStream();
    }

    @Override
    public String getContentType() {
        return file.getContentType();
    }

    @Override
    public String getOriginalFilename() {
        return file.getOriginalFilename();
    }

    @Override
    public Long getSize() {
        return file.getSize();
    }

    @Override
    public boolean isEmpty() {
        return file.isEmpty();
    }
}
