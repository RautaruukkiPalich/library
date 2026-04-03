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
public class AuthControllerIntegrationValidationTest {
    @Autowired
    private MockMvc mockMvc;

    private final static String BASE_API_AUTH_PATH = "/api/auth";

    @TestFactory
    Stream<DynamicTest> loginFormValidationFields_shouldReturn400() throws Exception {

        final String loginPath = BASE_API_AUTH_PATH + "/login";

        List<IntegrationTestCase> tcs = List.of(
                IntegrationTestCase.builder()
                        .desc("on no email expect validation error")
                        .jsonBody("{\"password\":\"QWErty123\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.email").isNotEmpty(),
                                jsonPath("$.validation_errors.email").isString(),
                                jsonPath("$.validation_errors.email").value("is required")
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on empty email expect validation error")
                        .jsonBody("{\"email\":\"\", \"password\":\"QWErty123\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.email").isNotEmpty(),
                                jsonPath("$.validation_errors.email").isString(),
                                jsonPath("$.validation_errors.email", CoreMatchers.containsString("is required"))
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on too short email expect validation error")
                        .jsonBody("{\"email\":\"123\", \"password\":\"QWErty123\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.email").isNotEmpty(),
                                jsonPath("$.validation_errors.email").isString(),
                                jsonPath("$.validation_errors.email", CoreMatchers.containsString("must be between 5 and 100 characters"))
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on invalid email format expect validation error")
                        .jsonBody("{\"email\":\"123456\", \"password\":\"QWErty123\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.email").isNotEmpty(),
                                jsonPath("$.validation_errors.email").isString(),
                                jsonPath("$.validation_errors.email", CoreMatchers.containsString("must match \"^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]+$\""))
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on no password expect validation error")
                        .jsonBody("{\"email\":\"123456@123.com\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.password").isNotEmpty(),
                                jsonPath("$.validation_errors.password").isString(),
                                jsonPath("$.validation_errors.password").value("is required")
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on empty password expect validation error")
                        .jsonBody("{\"email\":\"123456@123.com\", \"password\":\"\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.password").isNotEmpty(),
                                jsonPath("$.validation_errors.password").isString(),
                                jsonPath("$.validation_errors.password", CoreMatchers.containsString("is required"))
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on short password expect validation error")
                        .jsonBody("{\"email\":\"123456@123.com\", \"password\":\"1\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.password").isNotEmpty(),
                                jsonPath("$.validation_errors.password").isString(),
                                jsonPath("$.validation_errors.password", CoreMatchers.containsString("must be between 8 and 100 characters"))
                        ))
                        .build()
        );

        return tcs.stream().map(tc -> DynamicTest.dynamicTest(tc.desc(), () ->
                mockMvc.perform(
                                post(loginPath)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(tc.jsonBody()))
                        .andExpect(tc.statusMatcher())
                        .andExpectAll(
                                tc.combineWith(
                                        jsonPath("error").value("validation error"),
                                        jsonPath("message").value("invalid argument parameter"),
                                        jsonPath("path").value(loginPath)
                                )
                        )
        ));
    }

    @TestFactory
    Stream<DynamicTest> registerFormValidationFields_shouldReturn400() throws Exception {
        final String registerPath = BASE_API_AUTH_PATH + "/register";

        List<IntegrationTestCase> tcs = List.of(
                // Email validation tests (inherited from Login)
                IntegrationTestCase.builder()
                        .desc("on no email expect validation error")
                        .jsonBody("{\"firstname\":\"Alexander\",\"lastname\":\"Pushkin\",\"surname\":\"Sergeevich\",\"password\":\"QWErty123\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.email").isNotEmpty(),
                                jsonPath("$.validation_errors.email").isString(),
                                jsonPath("$.validation_errors.email").value("is required")
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on empty email expect validation error")
                        .jsonBody("{\"email\":\"\",\"firstname\":\"Alexander\",\"lastname\":\"Pushkin\",\"surname\":\"Sergeevich\",\"password\":\"QWErty123\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.email").isNotEmpty(),
                                jsonPath("$.validation_errors.email").isString(),
                                jsonPath("$.validation_errors.email", CoreMatchers.containsString("is required"))
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on too short email expect validation error")
                        .jsonBody("{\"email\":\"123\",\"firstname\":\"Alexander\",\"lastname\":\"Pushkin\",\"surname\":\"Sergeevich\",\"password\":\"QWErty123\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.email").isNotEmpty(),
                                jsonPath("$.validation_errors.email").isString(),
                                jsonPath("$.validation_errors.email", CoreMatchers.containsString("must be between 5 and 100 characters"))
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on invalid email format expect validation error")
                        .jsonBody("{\"email\":\"123456\",\"firstname\":\"Alexander\",\"lastname\":\"Pushkin\",\"surname\":\"Sergeevich\",\"password\":\"QWErty123\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.email").isNotEmpty(),
                                jsonPath("$.validation_errors.email").isString(),
                                jsonPath("$.validation_errors.email", CoreMatchers.containsString("must match \"^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]+$\""))
                        ))
                        .build(),

                // Password validation tests (inherited from Login)
                IntegrationTestCase.builder()
                        .desc("on no password expect validation error")
                        .jsonBody("{\"email\":\"test@test.com\",\"firstname\":\"Alexander\",\"lastname\":\"Pushkin\",\"surname\":\"Sergeevich\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.password").isNotEmpty(),
                                jsonPath("$.validation_errors.password").isString(),
                                jsonPath("$.validation_errors.password").value("is required")
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on empty password expect validation error")
                        .jsonBody("{\"email\":\"test@test.com\",\"password\":\"\",\"firstname\":\"Alexander\",\"lastname\":\"Pushkin\",\"surname\":\"Sergeevich\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.password").isNotEmpty(),
                                jsonPath("$.validation_errors.password").isString(),
                                jsonPath("$.validation_errors.password", CoreMatchers.containsString("is required"))
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on too short password expect validation error")
                        .jsonBody("{\"email\":\"test@test.com\",\"password\":\"1\",\"firstname\":\"Alexander\",\"lastname\":\"Pushkin\",\"surname\":\"Sergeevich\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.password").isNotEmpty(),
                                jsonPath("$.validation_errors.password").isString(),
                                jsonPath("$.validation_errors.password", CoreMatchers.containsString("must be between 8 and 100 characters"))
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on password without uppercase expect validation error")
                        .jsonBody("{\"email\":\"test@test.com\",\"password\":\"qwerty123\",\"firstname\":\"Alexander\",\"lastname\":\"Pushkin\",\"surname\":\"Sergeevich\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.password").isNotEmpty(),
                                jsonPath("$.validation_errors.password").isString(),
                                jsonPath("$.validation_errors.password", CoreMatchers.containsString("must contain uppercase letter"))
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on password without lowercase expect validation error")
                        .jsonBody("{\"email\":\"test@test.com\",\"password\":\"QWERTY123\",\"firstname\":\"Alexander\",\"lastname\":\"Pushkin\",\"surname\":\"Sergeevich\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.password").isNotEmpty(),
                                jsonPath("$.validation_errors.password").isString(),
                                jsonPath("$.validation_errors.password", CoreMatchers.containsString("must contain lowercase letter"))
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on password without digit expect validation error")
                        .jsonBody("{\"email\":\"test@test.com\",\"password\":\"QWErtyQWErty\",\"firstname\":\"Alexander\",\"lastname\":\"Pushkin\",\"surname\":\"Sergeevich\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.password").isNotEmpty(),
                                jsonPath("$.validation_errors.password").isString(),
                                jsonPath("$.validation_errors.password", CoreMatchers.containsString("must contain digit"))
                        ))
                        .build(),

                // Firstname validation tests
                IntegrationTestCase.builder()
                        .desc("on no firstname expect validation error")
                        .jsonBody("{\"email\":\"test@test.com\",\"password\":\"QWErty123\",\"lastname\":\"Pushkin\",\"surname\":\"Sergeevich\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.firstname").isNotEmpty(),
                                jsonPath("$.validation_errors.firstname").isString(),
                                jsonPath("$.validation_errors.firstname").value("firstname is required")
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on empty firstname expect validation error")
                        .jsonBody("{\"email\":\"test@test.com\",\"password\":\"QWErty123\",\"firstname\":\"\",\"lastname\":\"Pushkin\",\"surname\":\"Sergeevich\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.firstname").isNotEmpty(),
                                jsonPath("$.validation_errors.firstname").isString(),
                                jsonPath("$.validation_errors.firstname", CoreMatchers.containsString("firstname is required"))
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on too short firstname expect validation error")
                        .jsonBody("{\"email\":\"test@test.com\",\"password\":\"QWErty123\",\"firstname\":\"A\",\"lastname\":\"Pushkin\",\"surname\":\"Sergeevich\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.firstname").isNotEmpty(),
                                jsonPath("$.validation_errors.firstname").isString(),
                                jsonPath("$.validation_errors.firstname", CoreMatchers.containsString("must be between 2 and 100 characters"))
                        ))
                        .build(),

                // Lastname validation tests
                IntegrationTestCase.builder()
                        .desc("on no lastname expect validation error")
                        .jsonBody("{\"email\":\"test@test.com\",\"password\":\"QWErty123\",\"firstname\":\"Alexander\",\"surname\":\"Sergeevich\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.lastname").isNotEmpty(),
                                jsonPath("$.validation_errors.lastname").isString(),
                                jsonPath("$.validation_errors.lastname").value("lastname is required")
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on empty lastname expect validation error")
                        .jsonBody("{\"email\":\"test@test.com\",\"password\":\"QWErty123\",\"firstname\":\"Alexander\",\"lastname\":\"\",\"surname\":\"Sergeevich\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.lastname").isNotEmpty(),
                                jsonPath("$.validation_errors.lastname").isString(),
                                jsonPath("$.validation_errors.lastname", CoreMatchers.containsString("lastname is required"))
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on too short lastname expect validation error")
                        .jsonBody("{\"email\":\"test@test.com\",\"password\":\"QWErty123\",\"firstname\":\"Alexander\",\"lastname\":\"P\",\"surname\":\"Sergeevich\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.lastname").isNotEmpty(),
                                jsonPath("$.validation_errors.lastname").isString(),
                                jsonPath("$.validation_errors.lastname", CoreMatchers.containsString("must be between 2 and 100 characters"))
                        ))
                        .build(),

                // Surname validation tests
                IntegrationTestCase.builder()
                        .desc("on no surname expect validation error")
                        .jsonBody("{\"email\":\"test@test.com\",\"password\":\"QWErty123\",\"firstname\":\"Alexander\",\"lastname\":\"Pushkin\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.surname").isNotEmpty(),
                                jsonPath("$.validation_errors.surname").isString(),
                                jsonPath("$.validation_errors.surname").value("surname is required")
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on empty surname expect validation error")
                        .jsonBody("{\"email\":\"test@test.com\",\"password\":\"QWErty123\",\"firstname\":\"Alexander\",\"lastname\":\"Pushkin\",\"surname\":\"\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.surname").isNotEmpty(),
                                jsonPath("$.validation_errors.surname").isString(),
                                jsonPath("$.validation_errors.surname", CoreMatchers.containsString("surname is required"))
                        ))
                        .build(),

                IntegrationTestCase.builder()
                        .desc("on too short surname expect validation error")
                        .jsonBody("{\"email\":\"test@test.com\",\"password\":\"QWErty123\",\"firstname\":\"Alexander\",\"lastname\":\"Pushkin\",\"surname\":\"S\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.surname").isNotEmpty(),
                                jsonPath("$.validation_errors.surname").isString(),
                                jsonPath("$.validation_errors.surname", CoreMatchers.containsString("must be between 2 and 100 characters"))
                        ))
                        .build(),

                // Multiple validation errors test
                IntegrationTestCase.builder()
                        .desc("on multiple invalid fields expect multiple validation errors")
                        .jsonBody("{\"email\":\"invalid\",\"password\":\"weak\",\"firstname\":\"A\",\"lastname\":\"P\",\"surname\":\"S\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.email").isNotEmpty(),
                                jsonPath("$.validation_errors.password").isNotEmpty(),
                                jsonPath("$.validation_errors.firstname").isNotEmpty(),
                                jsonPath("$.validation_errors.lastname").isNotEmpty(),
                                jsonPath("$.validation_errors.surname").isNotEmpty()
                        ))
                        .build()
        );

        return tcs.stream().map(tc -> DynamicTest.dynamicTest(tc.desc(), () ->
                mockMvc.perform(
                                post(registerPath)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(tc.jsonBody()))
                        .andExpect(tc.statusMatcher())
                        .andExpectAll(
                                tc.combineWith(
                                        jsonPath("error").value("validation error"),
                                        jsonPath("message").value("invalid argument parameter"),
                                        jsonPath("path").value(registerPath)
                                )
                        )
        ));
    }

    @TestFactory
    Stream<DynamicTest> refreshTokenFormValidationFields_shouldReturn400() throws Exception {

        final String refreshTokenPath = BASE_API_AUTH_PATH + "/refresh-tokens";

        List<IntegrationTestCase> tcs = List.of(
                IntegrationTestCase.builder()
                        .desc("on no refresh token expect validation error")
                        .jsonBody("{}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.refresh_token").isNotEmpty(),
                                jsonPath("$.validation_errors.refresh_token").isString(),
                                jsonPath("$.validation_errors.refresh_token").value("is required")
                        ))
                        .build(),
                IntegrationTestCase.builder()
                        .desc("on empty refresh token expect validation error")
                        .jsonBody("{\"refresh_token\":\"\"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                jsonPath("$.validation_errors.refresh_token").isNotEmpty(),
                                jsonPath("$.validation_errors.refresh_token").isString(),
                                jsonPath("$.validation_errors.refresh_token", CoreMatchers.containsString("is required"))
                        ))
                        .build(),
                IntegrationTestCase.builder()
                        .desc("on null refresh token expect validation error")
                        .jsonBody("{\"refresh_token\":null}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                        jsonPath("$.validation_errors.refresh_token").isNotEmpty(),
                                        jsonPath("$.validation_errors.refresh_token").isString(),
                                        jsonPath("$.validation_errors.refresh_token").value("is required")
                                )
                        )
                        .build(),
                IntegrationTestCase.builder()
                        .desc("on whitespace refresh token expect validation error")
                        .jsonBody("{\"refresh_token\":\"   \"}")
                        .statusMatcher(status().isBadRequest())
                        .matchers(List.of(
                                        jsonPath("$.validation_errors.refresh_token").isNotEmpty(),
                                        jsonPath("$.validation_errors.refresh_token").isString(),
                                        jsonPath("$.validation_errors.refresh_token", CoreMatchers.containsString("is required"))
                                )
                        )
                        .build()
        );

        return tcs.stream().map(tc -> DynamicTest.dynamicTest(tc.desc(), () ->
                mockMvc.perform(
                                post(refreshTokenPath)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(tc.jsonBody()))
                        .andExpect(tc.statusMatcher())
                        .andExpectAll(
                                tc.combineWith(
                                        jsonPath("error").value("validation error"),
                                        jsonPath("message").value("invalid argument parameter"),
                                        jsonPath("path").value(refreshTokenPath)
                                )
                        )
        ));
    }
}