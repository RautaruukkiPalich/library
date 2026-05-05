package com.app.modules.media.dto;

import java.io.InputStream;

public record DownloadMediaDTO(
        InputStream stream,
        MediaFileDTO mediaFile
) {
}
