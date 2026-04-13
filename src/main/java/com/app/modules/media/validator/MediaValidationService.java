package com.app.modules.media.validator;


import com.app.modules.media.enums.MediaContent;
import com.app.modules.media.exceptions.FileValidationException;
import com.app.modules.media.source.MediaSource;
import com.app.modules.media.validator.base.MediaValidator;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MediaValidationService {
    private final List<MediaValidator> validators;
    private Map<MediaContent, MediaValidator> validatorMap;

    @PostConstruct
    public void init() {
        validatorMap = validators.stream()
                .collect(Collectors.toMap(
                        MediaValidator::getSupportedType,
                        Function.identity()
                ));
        log.info("registered {} media validators", validatorMap.size());
    }

    public void validate(@NonNull MediaSource source,
                         @NonNull MediaContent type)
            throws FileValidationException, IllegalArgumentException {
        MediaValidator validator = validatorMap.get(type);
        if (validator == null) {
            log.warn("no validator found for type: {}", type);
            throw new IllegalArgumentException("unsupported media type: " + type);
        }

        validator.validate(source);
    }
}
