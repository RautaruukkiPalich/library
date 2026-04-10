package com.app.modules.media.controller;

import com.app.core.annotation.public_endpoint.PublicEndpoint;
import com.app.core.aop.require_role.RequireRole;
import com.app.core.exception.ForbiddenException;
import com.app.core.security.rbac.Role;
import com.app.modules.media.api.FileService;
import com.app.modules.media.api.MediaService;
import com.app.modules.media.api.TaskService;
import com.app.modules.media.api.UploadService;
import com.app.modules.media.dto.MediaDTO;
import com.app.modules.media.dto.MediaFileDTO;
import com.app.modules.media.dto.TaskStatusDTO;
import com.app.modules.media.dto.UploadMediaDTO;
import com.app.modules.media.enums.MediaContentType;
import com.app.modules.media.enums.MediaPurpose;
import com.app.modules.media.enums.MediaSize;
import com.app.modules.media.exceptions.MediaFileNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static com.app.core.config.OpenAPIConfig.BEARER_SECURITY_SCHEME_NAME;

@RestController
@RequestMapping("/api/media")
@SecurityRequirement(name = BEARER_SECURITY_SCHEME_NAME)
@AllArgsConstructor
@Slf4j
@Tag(name = "media", description = "media api methods")
public class MediaController {

    private final UploadService uploadService;
    private final TaskService taskService;
    private final FileService fileService;
    private final MediaService mediaService;

    private final static String CHECK_UPLOAD_STATUS_TASK_PATH = "/api/media/tasks/";

    @PostMapping(value = "/users/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "post user avatar")
    @RequireRole(value = Role.USER)
    @ApiResponse(responseCode = "202", description = "accepted",
            content = @Content(schema = @Schema(implementation = MediaControllerDTO.Response.TaskStatus.class)))
    @ApiResponse(responseCode = "401", description = "unauthorized")
    @ApiResponse(responseCode = "403", description = "forbidden")
    @ApiResponse(responseCode = "404", description = "not found")
    @ApiResponse(responseCode = "415", description = "unsupported media type")
    public ResponseEntity<MediaControllerDTO.Response.TaskStatus> postUserAvatar(
            @AuthenticationPrincipal Long initiatorId,
            @RequestPart("file") MultipartFile file
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
                MediaControllerDTO.Response.TaskStatus
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
            content = @Content(schema = @Schema(implementation = MediaControllerDTO.Response.TaskStatus.class)))
    @ApiResponse(responseCode = "401", description = "unauthorized")
    @ApiResponse(responseCode = "403", description = "forbidden")
    @ApiResponse(responseCode = "404", description = "not found")
    public ResponseEntity<MediaControllerDTO.Response.TaskStatus> getTaskInfo(
            @AuthenticationPrincipal Long initiatorId,
            @PathVariable("taskUuid") UUID taskUUID
    ) {
        TaskStatusDTO taskStatus = taskService.status(taskUUID);

        if (!taskStatus.userId().equals(initiatorId)) {
            throw ForbiddenException.insufficientPermissions();
        }

        return ResponseEntity.ok().body(
                MediaControllerDTO.Response.TaskStatus.builder()
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
            content = @Content(schema = @Schema(implementation = MediaControllerDTO.Response.MediaItem.class)))
    @ApiResponse(responseCode = "401", description = "unauthorized")
    @ApiResponse(responseCode = "403", description = "forbidden")
    @ApiResponse(responseCode = "404", description = "not found")
    public ResponseEntity<MediaControllerDTO.Response.MediaItem> getMediaInfo(
            @AuthenticationPrincipal Long initiatorId,
            @PathVariable UUID mediaUuid
    ) {
        MediaDTO mediaDTO = mediaService.getMediaByUuid(mediaUuid);
        if (!mediaDTO.isPublic() && !mediaDTO.userId().equals(initiatorId)) {
            throw ForbiddenException.insufficientPermissions();
        }

        return ResponseEntity.ok().body(MediaControllerMapper.convert(mediaDTO));
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
            @RequestParam("size") String size,
            @RequestParam(value = "inline", defaultValue = "false") boolean inline
    ) {
        MediaSize mediaSize = MediaSize.fromCode(size);
        MediaDTO m = mediaService.getMediaByUuid(mediaUuid, mediaSize);

        if (!m.isPublic() && !m.userId().equals(initiatorId)) {
            log.warn("access denied: user={} tried to access media={} owned by={}",
                    initiatorId, mediaUuid, m.userId());
            throw ForbiddenException.insufficientPermissions();
        }

        if (m.files().isEmpty()) {
            log.warn("file not found: media={} size={}", mediaUuid, mediaSize);
            throw MediaFileNotFoundException.size(mediaSize);
        }

        MediaFileDTO mf = m.files().get(0);
        InputStream stream = fileService.download(mf.path());

        ContentDisposition contentDisposition = ContentDisposition
                .builder(inline ? "inline" : "attachment")
                .filename(mf.generateFilename())
                .filename(mf.generateFilename(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mf.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .contentLength(mf.fileSize())
                .body(new InputStreamResource(stream));
    }
}
