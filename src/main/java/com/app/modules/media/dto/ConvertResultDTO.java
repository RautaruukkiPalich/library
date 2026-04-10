package com.app.modules.media.dto;

import java.io.InputStream;

public record ConvertResultDTO(
        InputStream stream,
        Long size,
        Integer width,
        Integer height
) {
}
