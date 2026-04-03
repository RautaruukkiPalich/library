package com.app.utils;

import lombok.Builder;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Builder
public record IntegrationTestCase(
        String desc,
        String jsonBody,
        Map<String, String> headers,
        ResultMatcher statusMatcher,
        List<ResultMatcher> matchers
) {
    public ResultMatcher[] combineWith(ResultMatcher... commonMatchers) {
        return Stream.concat(
                Arrays.stream(commonMatchers),
                this.matchers.stream()
        ).toArray(ResultMatcher[]::new);
    }
}
