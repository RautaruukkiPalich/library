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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    Stream<DynamicTest> editPasswordValidationFields_shouldReturn400() throws Exception {
        final String editPasswordPath = BASE_API_USER_PATH + "/me/password";

        List<IntegrationTestCase> tcs = List.of(
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
                                put(editPasswordPath)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(tc.jsonBody()))
                        .andExpect(tc.statusMatcher())
                        .andExpectAll(
                                tc.combineWith(
                                        jsonPath("error").value("validation error"),
                                        jsonPath("message").value("invalid argument parameter"),
                                        jsonPath("path").value(editPasswordPath)
                                )
                        )
        ));
    }

    @TestFactory
    Stream<DynamicTest> editFirstnameValidation_shouldReturn400() throws Exception {
        final String editFirstnamePath = BASE_API_USER_PATH + "/me/firstname";

        List<IntegrationTestCase> tcs = List.of(
                IntegrationTestCase.builder()
                        .desc("on no firstname expect validation error")
                        .jsonBody("{}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.firstname").isNotEmpty(),
                                jsonPath("$.validation_errors.firstname").value("is required")
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on empty firstname expect validation error")
                        .jsonBody("{\"firstname\":\"\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.firstname").isNotEmpty(),
                                jsonPath("$.validation_errors.firstname", CoreMatchers.containsString("is required"))
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on blank firstname expect validation error")
                        .jsonBody("{\"firstname\":\"   \"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.firstname").isNotEmpty(),
                                jsonPath("$.validation_errors.firstname", CoreMatchers.containsString("is required"))
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on too long firstname expect validation error")
                        .jsonBody("{\"firstname\":\"%s\"}".formatted("A".repeat(101)))
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.firstname").isNotEmpty(),
                                jsonPath("$.validation_errors.firstname", CoreMatchers.containsString("must be between"))
                        ))
                        .build()
        );

        return tcs.stream().map(tc -> DynamicTest.dynamicTest(tc.desc(), () ->
                mockMvc.perform(put(editFirstnamePath)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(tc.jsonBody()))
                        .andExpect(tc.statusMatcher())
                        .andExpectAll(
                                tc.combineWith(
                                        jsonPath("error").value("validation error"),
                                        jsonPath("message").value("invalid argument parameter"),
                                        jsonPath("path").value(editFirstnamePath)
                                )
                        )
        ));
    }

    @TestFactory
    Stream<DynamicTest> editLastnameValidation_shouldReturn400() throws Exception {
        final String editLastnamePath = BASE_API_USER_PATH + "/me/lastname";

        List<IntegrationTestCase> tcs = List.of(
                IntegrationTestCase.builder()
                        .desc("on no lastname expect validation error")
                        .jsonBody("{}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.lastname").isNotEmpty(),
                                jsonPath("$.validation_errors.lastname").value("is required")
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on empty lastname expect validation error")
                        .jsonBody("{\"lastname\":\"\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.lastname").isNotEmpty(),
                                jsonPath("$.validation_errors.lastname", CoreMatchers.containsString("is required"))
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on too long lastname expect validation error")
                        .jsonBody("{\"lastname\":\"%s\"}".formatted("B".repeat(101)))
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.lastname").isNotEmpty(),
                                jsonPath("$.validation_errors.lastname", CoreMatchers.containsString("must be between"))
                        ))
                        .build()
        );

        return tcs.stream().map(tc -> DynamicTest.dynamicTest(tc.desc(), () ->
                mockMvc.perform(put(editLastnamePath)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(tc.jsonBody()))
                        .andExpect(tc.statusMatcher())
                        .andExpectAll(
                                tc.combineWith(
                                        jsonPath("error").value("validation error"),
                                        jsonPath("message").value("invalid argument parameter"),
                                        jsonPath("path").value(editLastnamePath)
                                )
                        )
        ));
    }

    @TestFactory
    Stream<DynamicTest> editSurnameValidation_shouldReturn400() throws Exception {
        final String editSurnamePath = BASE_API_USER_PATH + "/me/surname";

        List<IntegrationTestCase> tcs = List.of(
                IntegrationTestCase.builder()
                        .desc("on no surname expect validation error")
                        .jsonBody("{}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.surname").isNotEmpty(),
                                jsonPath("$.validation_errors.surname").value("is required")
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on empty surname expect validation error")
                        .jsonBody("{\"surname\":\"\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.surname").isNotEmpty(),
                                jsonPath("$.validation_errors.surname", CoreMatchers.containsString("is required"))
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on too long surname expect validation error")
                        .jsonBody("{\"surname\":\"%s\"}".formatted("C".repeat(101)))
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.surname").isNotEmpty(),
                                jsonPath("$.validation_errors.surname", CoreMatchers.containsString("must be between"))
                        ))
                        .build()
        );

        return tcs.stream().map(tc -> DynamicTest.dynamicTest(tc.desc(), () ->
                mockMvc.perform(put(editSurnamePath)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(tc.jsonBody()))
                        .andExpect(tc.statusMatcher())
                        .andExpectAll(
                                tc.combineWith(
                                        jsonPath("error").value("validation error"),
                                        jsonPath("message").value("invalid argument parameter"),
                                        jsonPath("path").value(editSurnamePath)
                                )
                        )
        ));
    }

    @TestFactory
    Stream<DynamicTest> editEmailValidation_shouldReturn400() throws Exception {
        final String editEmailPath = BASE_API_USER_PATH + "/me/email";

        List<IntegrationTestCase> tcs = List.of(
                IntegrationTestCase.builder()
                        .desc("on no email expect validation error")
                        .jsonBody("{}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.email").isNotEmpty(),
                                jsonPath("$.validation_errors.email").value("is required")
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on empty email expect validation error")
                        .jsonBody("{\"email\":\"\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.email").isNotEmpty(),
                                jsonPath("$.validation_errors.email", CoreMatchers.containsString("is required"))
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on invalid email format expect validation error")
                        .jsonBody("{\"email\":\"invalid-email\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.email").isNotEmpty(),
                                jsonPath("$.validation_errors.email", CoreMatchers.containsString("invalid format. expect 'test@test.test'"))
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on email without @ expect validation error")
                        .jsonBody("{\"email\":\"test.test.com\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.email").isNotEmpty()
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on too long email expect validation error")
                        .jsonBody("{\"email\":\"%s\"}".formatted("a".repeat(250) + "@test.com"))
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.email").isNotEmpty(),
                                jsonPath("$.validation_errors.email", CoreMatchers.containsString("must be between"))
                        ))
                        .build()
        );

        return tcs.stream().map(tc -> DynamicTest.dynamicTest(tc.desc(), () ->
                mockMvc.perform(put(editEmailPath)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(tc.jsonBody()))
                        .andExpect(tc.statusMatcher())
                        .andExpectAll(
                                tc.combineWith(
                                        jsonPath("error").value("validation error"),
                                        jsonPath("message").value("invalid argument parameter"),
                                        jsonPath("path").value(editEmailPath)
                                )
                        )
        ));
    }

    @TestFactory
    Stream<DynamicTest> multipleFieldsEditValidation_shouldReturn400() throws Exception {
        final String editFirstnamePath = BASE_API_USER_PATH + "/me/firstname";

        List<IntegrationTestCase> tcs = List.of(
                IntegrationTestCase.builder()
                        .desc("on wrong field name should still validate required fields")
                        .jsonBody("{\"wrong_field\":\"John\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.firstname").isNotEmpty()
                        ))
                        .build()
        );

        return tcs.stream().map(tc -> DynamicTest.dynamicTest(tc.desc(), () ->
                mockMvc.perform(put(editFirstnamePath)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(tc.jsonBody()))
                        .andExpect(tc.statusMatcher())
        ));
    }
}