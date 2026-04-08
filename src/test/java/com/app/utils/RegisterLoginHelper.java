package com.app.utils;

import com.app.BaseTest;
import com.app.modules.auth.dto.AuthControllerDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Builder
public class RegisterLoginHelper extends BaseTest {

    @Getter
    @Setter
    private String accessToken;
    @Getter
    @Setter
    private String refreshToken;

    private MockMvc mockMvc;

    private String firstname;
    private String lastname;
    private String surname;
    private String email;
    private String password;
    @Getter
    private Long id;

    private final static String BASE_API_AUTH_PATH = "/api/auth";

    private final static String registerJsonTmpl = "{\"email\":\"%s\",\"password\":\"%s\",\"firstname\":\"%s\",\"lastname\":\"%s\",\"surname\":\"%s\"}";
    private final static String loginJsonTmpl = "{\"email\":\"%s\",\"password\":\"%s\"}";
    private final static String refreshTokenTmpl = "{\"refresh_token\":\"%s\"}";

    public void registerUser() throws Exception {
        mockMvc.perform(
                        post(BASE_API_AUTH_PATH + "/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(registerJsonTmpl.formatted(email, password, firstname, lastname, surname)))
                .andExpect(status().isCreated());
    }

    public void loginUser() throws Exception {
        String body = mockMvc.perform(
                        post(BASE_API_AUTH_PATH + "/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginJsonTmpl.formatted(email, password)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var unmarshalled = unmarshall(body, AuthControllerDTO.TokenPairResponse.class);
//
//        AuthControllerDTO.TokenPairResponse deserialized = objectMapper
//                .readValue(respBody, AuthControllerDTO.TokenPairResponse.class);

        accessToken = unmarshalled.getAccessToken();
        refreshToken = unmarshalled.getRefreshToken();
    }

    public void refreshTokens() throws Exception {
        String respBody = mockMvc.perform(
                        post(BASE_API_AUTH_PATH + "/refresh-tokens")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(refreshTokenTmpl.formatted(refreshToken)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var unmarshalled = unmarshall(respBody, AuthControllerDTO.TokenPairResponse.class);
//
//        AuthControllerDTO.TokenPairResponse deserialized = objectMapper
//                .readValue(respBody, AuthControllerDTO.TokenPairResponse.class);

        accessToken = unmarshalled.getAccessToken();
        refreshToken = unmarshalled.getRefreshToken();
    }
}
