package com.app.modules.media.dto;

import lombok.*;

public class MediaQueryParamsDTO {

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DownloadParams {
        private String size;
    }
}
