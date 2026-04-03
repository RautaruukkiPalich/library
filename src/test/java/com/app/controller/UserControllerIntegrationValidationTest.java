package com.app.controller;


import com.app.config.TestSecurityConfig;
import com.app.utils.IntegrationTestCase;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
public class UserControllerIntegrationValidationTest {

    @Autowired
    private MockMvc mockMvc;

    private final static String BASE_API_USER_PATH = "/api/users";

    @TestFactory
    Stream<DynamicTest> changePasswordValidationFields_shouldReturn400() throws Exception {
        final String changePasswordPath = BASE_API_USER_PATH + "/me/change-password";

        List<IntegrationTestCase> tcs = List.of(
                // Old password validation tests
                IntegrationTestCase.builder()
                        .desc("on no old_password expect validation error")
                        .jsonBody("{\"password\":\"QWErty123\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.old_password").isNotEmpty(),
                                jsonPath("$.validation_errors.old_password").isString(),
                                jsonPath("$.validation_errors.old_password").value("is required")
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on empty old_password expect validation error")
                        .jsonBody("{\"old_password\":\"\", \"password\":\"QWErty123\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.old_password").isNotEmpty(),
                                jsonPath("$.validation_errors.old_password").isString(),
                                jsonPath("$.validation_errors.old_password", CoreMatchers.containsString("is required"))
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on too short old_password expect validation error")
                        .jsonBody("{\"old_password\":\"1\", \"password\":\"QWErty123\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.old_password").isNotEmpty(),
                                jsonPath("$.validation_errors.old_password").isString(),
                                jsonPath("$.validation_errors.old_password", CoreMatchers.containsString("must be between 8 and 100 characters"))
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on old_password without uppercase expect validation error")
                        .jsonBody("{\"old_password\":\"qwerty123\", \"password\":\"QWErty123\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.old_password").isNotEmpty(),
                                jsonPath("$.validation_errors.old_password").isString(),
                                jsonPath("$.validation_errors.old_password", CoreMatchers.containsString("must contain uppercase letter"))
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on old_password without lowercase expect validation error")
                        .jsonBody("{\"old_password\":\"QWERTY123\", \"password\":\"QWErty123\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.old_password").isNotEmpty(),
                                jsonPath("$.validation_errors.old_password").isString(),
                                jsonPath("$.validation_errors.old_password", CoreMatchers.containsString("must contain lowercase letter"))
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on old_password without digit expect validation error")
                        .jsonBody("{\"old_password\":\"QWErtyQWErty\", \"password\":\"QWErty123\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.old_password").isNotEmpty(),
                                jsonPath("$.validation_errors.old_password").isString(),
                                jsonPath("$.validation_errors.old_password", CoreMatchers.containsString("must contain digit"))
                        ))
                        .build(),

                // New password validation tests
                IntegrationTestCase.builder()
                        .desc("on no password expect validation error")
                        .jsonBody("{\"old_password\":\"QWErty123\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.password").isNotEmpty(),
                                jsonPath("$.validation_errors.password").isString(),
                                jsonPath("$.validation_errors.password").value("is required")
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on empty password expect validation error")
                        .jsonBody("{\"old_password\":\"QWErty123\", \"password\":\"\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.password").isNotEmpty(),
                                jsonPath("$.validation_errors.password").isString(),
                                jsonPath("$.validation_errors.password", CoreMatchers.containsString("is required"))
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on too short password expect validation error")
                        .jsonBody("{\"old_password\":\"QWErty123\", \"password\":\"1\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.password").isNotEmpty(),
                                jsonPath("$.validation_errors.password").isString(),
                                jsonPath("$.validation_errors.password", CoreMatchers.containsString("must be between 2 and 100 characters"))
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on password without uppercase expect validation error")
                        .jsonBody("{\"old_password\":\"QWErty123\", \"password\":\"qwerty123\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.password").isNotEmpty(),
                                jsonPath("$.validation_errors.password").isString(),
                                jsonPath("$.validation_errors.password", CoreMatchers.containsString("must contain uppercase letter"))
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on password without lowercase expect validation error")
                        .jsonBody("{\"old_password\":\"QWErty123\", \"password\":\"QWERTY123\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.password").isNotEmpty(),
                                jsonPath("$.validation_errors.password").isString(),
                                jsonPath("$.validation_errors.password", CoreMatchers.containsString("must contain lowercase letter"))
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on password without digit expect validation error")
                        .jsonBody("{\"old_password\":\"QWErty123\", \"password\":\"QWErtyQWErty\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.password").isNotEmpty(),
                                jsonPath("$.validation_errors.password").isString(),
                                jsonPath("$.validation_errors.password", CoreMatchers.containsString("must contain digit"))
                        ))
                        .build(),

                // Multiple validation errors test
                IntegrationTestCase.builder()
                        .desc("on multiple invalid fields expect multiple validation errors")
                        .jsonBody("{\"old_password\":\"\", \"password\":\"\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.old_password").isNotEmpty(),
                                jsonPath("$.validation_errors.password").isNotEmpty()
                        ))
                        .build()
        );

        return tcs.stream().map(tc -> DynamicTest.dynamicTest(tc.desc(), () ->
                mockMvc.perform(
                                post(changePasswordPath)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(tc.jsonBody()))
                        .andExpect(tc.statusMatcher())
                        .andExpectAll(
                                tc.combineWith(
                                        jsonPath("error").value("validation error"),
                                        jsonPath("message").value("invalid argument parameter"),
                                        jsonPath("path").value(changePasswordPath)
                                )
                        )
        ));
    }
}