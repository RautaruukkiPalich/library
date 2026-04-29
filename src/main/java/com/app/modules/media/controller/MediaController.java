package com.app.modules.media.controller;

import com.app.core.annotation.api_response.ApiStandardErrorResponse;
import com.app.core.annotation.public_endpoint.PublicEndpoint;
import com.app.core.aop.require_role.RequireRole;
import com.app.core.response.ErrorResponse;
import com.app.core.security.rbac.Role;
import com.app.modules.media.converter.ConversionParams;
import com.app.modules.media.dto.DownloadMediaDTO;
import com.app.modules.media.dto.MediaDTO;
import com.app.modules.media.dto.MediaFileDTO;
import com.app.modules.media.dto.TaskStatusDTO;
import com.app.modules.media.enums.MediaSize;
import com.app.modules.media.enums.SortOrder;
import com.app.modules.media.enums.TaskStatus;
import com.app.modules.media.source.MultipartFileMediaSource;
import com.app.modules.media.usecase.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static com.app.core.config.OpenAPIConfig.BEARER_SECURITY_SCHEME_NAME;

@RestController
@RequestMapping("/api/media")
@SecurityRequirement(name = BEARER_SECURITY_SCHEME_NAME)
@AllArgsConstructor
@Slf4j
@Tag(name = "media", description = "media api methods")
public class MediaController {
    private final UploadMediaUseCase uploadMediaUseCase;
    private final GetUserTasksUseCase getUserTasksUseCase;
    private final GetUserTasksCountUseCase getUserTasksCountUseCase;
    private final GetTaskUseCase getTaskByUuidUseCase;
    private final GetMediaUseCase getMediaByUuidUseCase;
    private final DeleteMediaUseCase deleteMediaByUuidUseCase;
    private final PostMediaTaskUseCase postMediaTaskUseCase;
    private final DownloadMediaUseCase downloadMediaUseCase;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "upload media file")
    @RequireRole(value = Role.USER)
    @ApiResponse(responseCode = "202", description = "accepted",
            content = @Content(schema = @Schema(implementation = MediaControllerDTO.Response.MediaUUID.class)))
    @ApiResponse(responseCode = "415", description = "unsupported media type",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiStandardErrorResponse
    public ResponseEntity<MediaControllerDTO.Response.MediaUUID> uploadMedia(
            @AuthenticationPrincipal Long userId,
            @RequestPart("file") MultipartFile file
    ) {
        UUID mediaUuid = uploadMediaUseCase.execute(
                new UploadMediaUseCase.Input(
                        userId,
                        new MultipartFileMediaSource(file)));

        return ResponseEntity.accepted().body(
                new MediaControllerDTO.Response.MediaUUID(mediaUuid));
    }

    @GetMapping("/tasks")
    @RequireRole(Role.USER)
    @Operation(summary = "all user tasks info")
    @ApiResponse(responseCode = "200", description = "success",
            content = @Content(schema = @Schema(implementation = MediaControllerDTO.Response.TaskStatus.class)))
    @ApiStandardErrorResponse
    public ResponseEntity<List<MediaControllerDTO.Response.TaskStatus>> listTaskInfo(
            @AuthenticationPrincipal Long userId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
            @RequestParam(value = "status", required = false) TaskStatus status,
            @RequestParam(value = "order", required = false) SortOrder order
    ) {
        List<TaskStatusDTO> tasks = getUserTasksUseCase.execute(
                new GetUserTasksUseCase.Input(
                        userId, page, pageSize, status, order));

        return ResponseEntity.ok().body(tasks.stream().map(MediaControllerMapper::toResponse).toList());
    }

    @GetMapping("/tasks/count")
    @RequireRole(Role.USER)
    @Operation(summary = "all user tasks info")
    @ApiResponse(responseCode = "200", description = "success",
            content = @Content(schema = @Schema(implementation = Long.class)))
    @ApiStandardErrorResponse
    public ResponseEntity<Long> taskCount(
            @AuthenticationPrincipal Long userId,
            @RequestParam(value = "status", required = false) TaskStatus status
    ) {
        Long count = getUserTasksCountUseCase.execute(
                new GetUserTasksCountUseCase.Input(userId, status));

        return ResponseEntity.ok().body(count);
    }

    @GetMapping("/tasks/{taskUuid}")
    @RequireRole(Role.USER)
    @Operation(summary = "upload task info")
    @ApiResponse(responseCode = "200", description = "success",
            content = @Content(schema = @Schema(implementation = MediaControllerDTO.Response.TaskStatus.class)))
    @ApiStandardErrorResponse
    public ResponseEntity<MediaControllerDTO.Response.TaskStatus> taskInfo(
            @AuthenticationPrincipal Long userId,
            @PathVariable("taskUuid") UUID taskUuid
    ) {
        TaskStatusDTO taskStatus = getTaskByUuidUseCase.execute(
                new GetTaskUseCase.Input(userId, taskUuid));

        return ResponseEntity.ok().body(MediaControllerMapper.toResponse(taskStatus));
    }

    @GetMapping("/{mediaUuid}")
    @PublicEndpoint
    @Operation(summary = "list files with sizes by media uuid")
    @ApiResponse(responseCode = "200", description = "success",
            content = @Content(schema = @Schema(implementation = MediaControllerDTO.Response.MediaItem.class)))
    @ApiStandardErrorResponse
    public ResponseEntity<MediaControllerDTO.Response.MediaItem> mediaInfo(
            @AuthenticationPrincipal Long userId,
            @PathVariable UUID mediaUuid
    ) {
        MediaDTO media = getMediaByUuidUseCase.execute(
                new GetMediaUseCase.Input(userId, mediaUuid));

        return ResponseEntity.ok().body(MediaControllerMapper.convert(media));
    }

    @DeleteMapping("/{mediaUuid}")
    @RequireRole(Role.USER)
    @Operation(summary = "delete media file")
    @ApiResponse(responseCode = "204", description = "success")
    @ApiStandardErrorResponse
    public ResponseEntity<Void> deleteMedia(
            @AuthenticationPrincipal Long userId,
            @PathVariable UUID mediaUuid
    ) {
        deleteMediaByUuidUseCase.execute(
                new DeleteMediaUseCase.Input(userId, mediaUuid));

        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{mediaUuid}/convert")
    @RequireRole(Role.USER)
    @Operation(summary = "create task")
    @ApiResponse(responseCode = "202", description = "accepted",
            content = @Content(schema = @Schema(implementation = MediaControllerDTO.Response.TaskStatus.class)))
    @ApiStandardErrorResponse
    public ResponseEntity<MediaControllerDTO.Response.TaskStatus> newTask(
            @AuthenticationPrincipal Long userId,
            @PathVariable UUID mediaUuid,
            @Valid @RequestBody MediaControllerDTO.Request.ConversionRequest body
    ) {
        ConversionParams cp = MediaControllerMapper.convert(body);

        TaskStatusDTO taskStatus = postMediaTaskUseCase.execute(
                new PostMediaTaskUseCase.Input(userId, mediaUuid, cp));

        return ResponseEntity.accepted().body(MediaControllerMapper.toResponse(taskStatus));
    }


    @GetMapping("/{mediaUuid}/download")
    @PublicEndpoint
    @Operation(summary = "download file")
    @ApiResponse(responseCode = "200", description = "success",
            content = @Content(mediaType = "application/octet-stream",
                    schema = @Schema(type = "string", format = "binary")))
    @ApiStandardErrorResponse
    public ResponseEntity<Resource> downloadFile(
            @AuthenticationPrincipal Long userId,
            @PathVariable UUID mediaUuid,
            @RequestParam(value = "size", required = false) MediaSize size,
            @RequestParam(value = "file_uuid", required = false) UUID fileUuid,
            @RequestParam(value = "inline", defaultValue = "false") boolean inline
    ) {
        DownloadMediaDTO dto = downloadMediaUseCase.execute(
                new DownloadMediaUseCase.Input(userId, mediaUuid, size, fileUuid));

        MediaFileDTO mf = dto.mediaFile();
        String generatedFilename = mf.generateFilename();

        ContentDisposition contentDisposition = ContentDisposition
                .builder(inline ? "inline" : "attachment")
                .filename(generatedFilename)
                .filename(generatedFilename, StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mf.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .contentLength(mf.metadata().getFileSize())
                .body(new InputStreamResource(dto.stream()));
    }
}
