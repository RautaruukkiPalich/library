package com.app.modules.media.service;

import com.app.core.exception.ForbiddenException;
import com.app.modules.media.model.Media;
import com.app.modules.media.model.MediaTask;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CheckPermissionService {

    public void checkTaskPermission(@NonNull MediaTask task,
                                    @NonNull Long userId) throws ForbiddenException {
        if (!task.getUserId().equals(userId)) {
            log.warn("access denied: user={} tried to access task={} owned by={}",
                    userId, task.getUuid(), task.getUserId());
            throw ForbiddenException.insufficientPermissions();
        }
    }

    public void checkCanView(@NonNull Media media,
                              @NonNull Long userId) throws ForbiddenException {
        if (!media.getIsPublic() && !media.getUserId().equals(userId)) {
            logAndThrow(media, userId, "view");
        }
    }

    public void checkCanEdit(@NonNull Media media,
                              @NonNull Long userId) throws ForbiddenException {
        if (!media.getUserId().equals(userId)) {
            logAndThrow(media, userId, "edit");
        }
    }

    private void logAndThrow(@NonNull Media media,
                             @NonNull Long userId,
                             @NonNull String action) throws ForbiddenException {
        log.warn("access denied: user={} tried to {} media={} owned by={}",
                userId, action, media.getUuid(), media.getUserId());
        throw ForbiddenException.insufficientPermissions();
    }
}
