package com.app.modules.media.controller;

import com.app.modules.media.enums.MediaSize;
import com.app.modules.media.metadata.MediaMetadata;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class MediaControllerDTO {
    public static class Request {
        @JsonTypeInfo(
                use = JsonTypeInfo.Id.NAME,
                include = JsonTypeInfo.As.PROPERTY,
                property = "type",
                visible = true
        )
        @JsonSubTypes({
                @JsonSubTypes.Type(value = ImageConversionRequest.class, name = "image"),
        })
        @Schema(
                description = "Base conversion request",
                discriminatorProperty = "type",
                discriminatorMapping = {
                        @DiscriminatorMapping(value = "image", schema = ImageConversionRequest.class)
                }
        )
        @Setter
        @Getter
        @NoArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public abstract static class ConversionRequest {

            @Schema(description = "type of media",
                    example = "image", requiredMode = Schema.RequiredMode.REQUIRED)
            private String type;

            @NotNull
            @JsonProperty("extension")
            @Schema(description = "target file extension",
                    example = "webp", defaultValue = "webp",
                    requiredMode = Schema.RequiredMode.REQUIRED)
            private String targetExtension;

            @JsonProperty("quality")
            @Min(1)
            @Max(100)
            @Schema(description = "conversion quality (1 - 100) default 100",
                    example = "42", defaultValue = "100")
            private Integer quality;
        }

        @Data
        @Setter
        @Getter
        @NoArgsConstructor
        @EqualsAndHashCode(callSuper = true)
        @Schema(description = "image conversion request", allOf = ConversionRequest.class)
        public static class ImageConversionRequest extends ConversionRequest {

            @NotNull
            @Min(1)
            @Max(10000)
            @Schema(description = "image width in pixels",
                    minimum = "1", maximum = "10000", requiredMode = Schema.RequiredMode.REQUIRED)
            private Integer width;

            @NotNull
            @Min(1)
            @Max(10000)
            @Schema(description = "image height in pixels",
                    minimum = "1", maximum = "10000", requiredMode = Schema.RequiredMode.REQUIRED)
            private Integer height;

            @Schema(description = "keep original aspect ratio", defaultValue = "true")
            private Boolean keepAspectRatio = true;

            @Schema(description = "crop to square", defaultValue = "false")
            private Boolean cropToSquare = false;
        }
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

            @JsonProperty("fail_reason")
            @Schema(description = "fail reason if exists")
            private String failReason;

            @JsonProperty("created_at")
            @Schema(description = "created at datetime", requiredMode = Schema.RequiredMode.REQUIRED)
            private OffsetDateTime createdAt;

            @JsonProperty("updated_at")
            @Schema(description = "updated at datetime", requiredMode = Schema.RequiredMode.REQUIRED)
            private OffsetDateTime updatedAt;
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

            @JsonProperty("metadata")
            @Schema(description = "file metadata", requiredMode = Schema.RequiredMode.REQUIRED)
            private MediaMetadata metadata;

            @JsonProperty("download_url")
            @Schema(description = "download url", requiredMode = Schema.RequiredMode.REQUIRED)
            private List<String> downloadUrl;
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
            private Integer totalCount;
        }
    }
}
