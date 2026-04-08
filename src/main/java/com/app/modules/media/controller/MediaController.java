package com.app.modules.media.controller;

import com.app.core.annotation.public_endpoint.PublicEndpoint;
import com.app.core.aop.require_role.RequireRole;
import com.app.core.exception.ForbiddenException;
import com.app.core.security.rbac.Role;
import com.app.modules.media.api.DownloadService;
import com.app.modules.media.api.UploadService;
import com.app.modules.media.dto.*;
import com.app.modules.media.enums.MediaContentType;
import com.app.modules.media.enums.MediaPurpose;
import com.app.modules.media.enums.MediaSize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
@RequestMapping("/api/media")
@AllArgsConstructor
@Tag(name = "media", description = "media api methods")
public class MediaController {

    private final UploadService uploadService;
    private final DownloadService downloadService;

    private final static String CHECK_UPLOAD_STATUS_TASK_PATH = "/api/media/tasks/";

    @PostMapping("/users/avatar")
    @Operation(summary = "post user avatar")
    @RequireRole(value = Role.USER)
    @ApiResponse(responseCode = "202", description = "accepted",
            content = @Content(schema = @Schema(implementation = MediaControllerDTO.Response.MediaUploadTaskStatus.class)))
    @ApiResponse(responseCode = "401", description = "unauthorized")
    @ApiResponse(responseCode = "403", description = "forbidden")
    @ApiResponse(responseCode = "404", description = "not found")
    public ResponseEntity<MediaControllerDTO.Response.MediaUploadTaskStatus> postUserAvatar(
            @AuthenticationPrincipal Long initiatorId,
            @RequestParam MultipartFile file
    ) {
        TaskStatusDTO taskStatus = uploadService.upload(UploadMediaDTO
                .builder()
                .userId(initiatorId)
                .purpose(MediaPurpose.USER_AVATAR)
                .type(MediaContentType.IMAGE)
                .file(file)
                .build()
        );

        return ResponseEntity.accepted().body(
                MediaControllerDTO.Response.MediaUploadTaskStatus
                        .builder()
                        .taskUUID(taskStatus.taskUUID())
                        .status(taskStatus.status().toString())
                        .statusCheckUrl(CHECK_UPLOAD_STATUS_TASK_PATH + taskStatus.taskUUID())
                        .build()
        );
    }


    @GetMapping("/tasks/{taskUuid}")
    @PublicEndpoint
    @Operation(summary = "get upload task info")
    @ApiResponse(responseCode = "200", description = "success",
            content = @Content(schema = @Schema(implementation = MediaControllerDTO.Response.MediaUploadTaskStatus.class)))
    @ApiResponse(responseCode = "401", description = "unauthorized")
    @ApiResponse(responseCode = "403", description = "forbidden")
    @ApiResponse(responseCode = "404", description = "not found")
    public ResponseEntity<MediaControllerDTO.Response.MediaUploadTaskStatus> getTaskInfo(
            @AuthenticationPrincipal Long initiatorId,
            @PathVariable("taskUuid") UUID taskUUID
    ) {
        TaskStatusDTO taskStatus = uploadService.taskStatus(taskUUID);

        if (!taskStatus.userId().equals(initiatorId)) {
            throw ForbiddenException.insufficientPermissions();
        }

        return ResponseEntity.ok().body(
                MediaControllerDTO.Response.MediaUploadTaskStatus.builder()
                        .taskUUID(taskStatus.taskUUID())
                        .mediaUUID(taskStatus.mediaUUID())
                        .status(taskStatus.status().toString())
                        .build()
        );
    }

    @GetMapping("/{mediaUuid}/files")
    @PublicEndpoint
    @Operation(summary = "get list files with sizes by media uuid")
    @ApiResponse(responseCode = "200", description = "success",
            content = @Content(schema = @Schema(implementation = MediaControllerDTO.Response.MediaItemsResponse.class)))
    @ApiResponse(responseCode = "401", description = "unauthorized")
    @ApiResponse(responseCode = "403", description = "forbidden")
    @ApiResponse(responseCode = "404", description = "not found")
    public ResponseEntity<MediaControllerDTO.Response.MediaItemsResponse> getListItems(
            @AuthenticationPrincipal Long initiatorId,
            @PathVariable UUID mediaUuid
    ) {

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{mediaUuid}/download")
    @PublicEndpoint
    @Operation(summary = "download file")
    @ApiResponse(responseCode = "200", description = "success",
            content = @Content(mediaType = "application/octet-stream",
                    schema = @Schema(type = "string", format = "binary")))
    @ApiResponse(responseCode = "401", description = "unauthorized")
    @ApiResponse(responseCode = "403", description = "forbidden")
    @ApiResponse(responseCode = "404", description = "not found")
    public ResponseEntity<Resource> downloadFile(
            @AuthenticationPrincipal Long initiatorId,
            @PathVariable UUID mediaUuid,
            @ModelAttribute MediaQueryParamsDTO.DownloadParams params
    ) {
        MediaSize size = MediaSize.fromCode(params.getSize());
        DownloadedMediaDTO res = downloadService.download(mediaUuid, initiatorId, size);

        String filename = "%s_%s.%s".formatted(mediaUuid, size.getCode(), res.extension());

        ContentDisposition contentDisposition = ContentDisposition
                .builder("attachment")
                .filename(filename)
                .filename(filename, StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(res.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .contentLength(res.fileSize())
                .body(res.file());
    }
}
