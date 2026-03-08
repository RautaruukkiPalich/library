package com.app.utils.validator;

import org.apache.logging.log4j.util.Strings;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class ObjectValidator<T> extends Validator<T, ObjectValidator<T>> {

    public ObjectValidator(String key, T value) {
        super(key, value);
    }

    public <N extends Number & Comparable<N>> ObjectValidator<T> validateNumber(
            Function<T, N> fieldExtractor,
            String fieldKey,
            Consumer<NumberValidator<N>> validatorConfig
    ) {
        N fieldValue = fieldExtractor.apply(value);
        NumberValidator<N> validator = new NumberValidator<>(fieldKey, fieldValue);
        validatorConfig.accept(validator);

        addCheck(() -> processValidationResult(validator.validate()));
        return this;
    }

    public ObjectValidator<T> validateString(
            Function<T, String> fieldExtractor,
            String fieldKey,
            Consumer<StringValidator> validatorConfig
    ) {
        String fieldValue = fieldExtractor.apply(value);
        StringValidator validator = new StringValidator(fieldKey, fieldValue);
        validatorConfig.accept(validator);

        addCheck(() -> processValidationResult(validator.validate()));
        return this;
    }

    private Map<String, String> processValidationResult(Map<String, String> result) {
        return Optional.ofNullable(result)
                .flatMap(
                        map -> map.entrySet().stream()
                                .filter(Objects::nonNull)
                                .findFirst())
                .map(entry -> Map.of(
                        key,
                        Strings.join(List.of(key, entry.getKey(), entry.getValue()), ' ')
                ))
                .orElse(null);
    }
}
