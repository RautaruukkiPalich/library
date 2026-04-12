package com.app.modules.media.controller;

import com.app.modules.media.enums.MediaSize;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
import java.util.UUID;

public class MediaControllerDTO {
    public static class Request {
//        @Setter
//        @Getter
//        @Builder
//        @NoArgsConstructor
//        @AllArgsConstructor
//        @JsonInclude(JsonInclude.Include.NON_NULL)
//        @Schema(name = "new task", description = "create new task for media")
//        public static class CreateTask {
//        }

    }

    public static class Response {
        @Setter
        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Schema(name = "media uuid", description = "uploaded media")
        public static class MediaUUID {
            @JsonProperty("media_uuid")
            @Schema(description = "media uuid")
            private UUID mediaUuid;
        }

        @Setter
        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Schema(name = "task status", description = "task task uploaded media")
        public static class TaskStatus {

            @JsonProperty("task_uuid")
            @Schema(description = "task uuid", requiredMode = Schema.RequiredMode.REQUIRED)
            private UUID taskUuid;

            @JsonProperty("media_uuid")
            @Schema(description = "media uuid")
            private UUID mediaUuid;

            @JsonProperty("status")
            @Schema(description = "task status", requiredMode = Schema.RequiredMode.REQUIRED)
            private String status;

            @JsonProperty("status_check_url")
            @Schema(description = "url to check task status")
            private String statusCheckUrl;
        }

        @Setter
        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Schema(name = "media size info", description = "media size info")
        public static class MediaSizeInfo {

            @JsonProperty("content_type")
            @Schema(description = "content type", requiredMode = Schema.RequiredMode.REQUIRED)
            private String contentType;

            @JsonProperty("media_size")
            @Schema(description = "media size", requiredMode = Schema.RequiredMode.REQUIRED)
            private MediaSize size;

            @JsonProperty("width")
            @Schema(description = "width in pixels")
            private Integer width;

            @JsonProperty("height")
            @Schema(description = "height in pixels")
            private Integer height;

            @JsonProperty("file_size")
            @Schema(description = "file size", requiredMode = Schema.RequiredMode.REQUIRED)
            private Long fileSize;

            @JsonProperty("download_url")
            @Schema(description = "download url", requiredMode = Schema.RequiredMode.REQUIRED)
            private String downloadUrl;
        }

        @Setter
        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @Schema(name = "media item")
        public static class MediaItem {

            @JsonProperty("media_uuid")
            @Schema(description = "media uuid")
            private UUID mediaUuid;

            @JsonProperty("user_id")
            @Schema(description = "owner user id")
            private Long userId;

            @JsonProperty("original_filename")
            @Schema(description = "original filename")
            private String originalFilename;

            @JsonProperty("sizes")
            @Schema(description = "available sizes and download url")
            private List<MediaSizeInfo> sizes;

            @JsonProperty("total_count")
            @Schema(description = "total count items")
            private Integer totalCount = 0;
        }
    }
}
