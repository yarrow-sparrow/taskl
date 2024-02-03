package com.github.straightth.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.straightth.MockMvcAbstractTest;
import com.github.straightth.authentication.WithUserMock;
import com.github.straightth.domain.User;
import com.github.straightth.dto.request.SignInRequest;
import com.github.straightth.dto.request.SignUpRequest;
import com.github.straightth.dto.response.SignInResponse;
import com.github.straightth.repository.UserRepository;
import com.github.straightth.service.authentication.JwtService;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthenticationControllerTest extends MockMvcAbstractTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    @Nested
    class SignUp {

        @Test
        public void userIsBeingRegistered() throws Exception {
            //Arrange
            var request = SignUpRequest.builder()
                    .username("User")
                    .email("email@email.com")
                    .password("pas3w@rD")
                    .build();

            var expectedUser = User.builder()
                    .username("User")
                    .email("email@email.com")
                    .password("pas3w@rD")
                    .build();

            //Act
            var result = mockMvc.perform(post("/v1/auth/sign-up")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            );

            //Assert
            result.andExpect(status().isOk());

            var actualUser = userRepository.findByEmail("email@email.com").orElseThrow();
            Assertions.assertThat(actualUser)
                    .usingRecursiveComparison()
                    .ignoringFields("id", "password")
                    .isEqualTo(expectedUser);
            Assertions.assertThat(passwordEncoder.matches("pas3w@rD", actualUser.getPassword())).isTrue();
        }

        @Test
        @WithUserMock(email = "explicit@email.com")
        public void alreadyUsedEmailLeadsTo409() throws Exception {
            //Arrange
            var request = SignUpRequest.builder()
                    .username("Username")
                    .email("explicit@email.com")
                    .password("pas3w@rD")
                    .build();

            //Act
            var result = mockMvc.perform(post("/v1/auth/sign-up")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            );

            //Assert
            result.andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message")
                            .value("Email already in use"));
        }

        @Nested
        class Validation {

            @Nested
            class Username {

                @Test
                public void emptyUsernameLeadsTo400() throws Exception {
                    //Arrange
                    var request = SignUpRequest.builder()
                            .username("")
                            .email("email@email.com")
                            .password("pas3w@rD")
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/auth/sign-up")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Username must be between 1 and 30 characters"));
                }

                @Test
                public void singleCharacterNameLeadsTo200() throws Exception {
                    //Arrange
                    var request = SignUpRequest.builder()
                            .username("*")
                            .email("email@email.com")
                            .password("pas3w@rD")
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/auth/sign-up")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isOk());
                }

                @Test
                public void usernameEqualToLimitLeadsTo200() throws Exception {
                    //Arrange
                    var request = SignUpRequest.builder()
                            .username(StringUtils.repeat("*", 30))
                            .email("email@email.com")
                            .password("pas3w@rD")
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/auth/sign-up")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isOk());
                }

                @Test
                public void usernameLongerThanLimitLeadsTo400() throws Exception {
                    //Arrange
                    var request = SignUpRequest.builder()
                            .username(StringUtils.repeat("*", 31))
                            .email("email@email.com")
                            .password("pas3w@rD")
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/auth/sign-up")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Username must be between 1 and 30 characters"));
                }
            }

            @Nested
            class Email {

                @Test
                public void emptyEmailLeadsTo400() throws Exception {
                    //Arrange
                    var request = SignUpRequest.builder()
                            .username("Username")
                            .email("")
                            .password("pas3w@rD")
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/auth/sign-up")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Email must be valid in format, example: tomas@gmail.com"));
                }

                @Test
                public void invalidEmailLeadsTo400() throws Exception {
                    //Arrange
                    var request = SignUpRequest.builder()
                            .username("Username")
                            .email("invalid-email.")
                            .password("pas3w@rD")
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/auth/sign-up")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Email must be valid in format, example: tomas@gmail.com"));
                }
            }

            @Nested
            class Password {

                @Test
                public void notEnoughSecuredPasswordLeadsTo400() throws Exception {
                    //Arrange
                    var request = SignUpRequest.builder()
                            .username("Username")
                            .email("email@email.com")
                            .password("password")
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/auth/sign-up")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Password must have minimum eight characters, "
                                            + "at least one upper case English letter, one lower case English letter, "
                                            + "one number and one special character"
                                    ));
                }
            }
        }
    }

    @Nested
    class SignIn {

        @Test
        @WithUserMock(email = "explicit@email.com", rawPassword = "pas3w@rD")
        public void userIsBeingLoggedIn() throws Exception {
            //Arrange
            var mockedUser = getMockedUser();
            var request = SignInRequest.builder()
                    .email("explicit@email.com")
                    .password("pas3w@rD")
                    .build();

            //Act
            var result = mockMvc.perform(post("/v1/auth/sign-in")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            );

            //Assert
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").isNotEmpty());

            var response = objectMapper.readValue(
                    result.andReturn().getResponse().getContentAsString(),
                    SignInResponse.class
            );
            Assertions.assertThat(jwtService.isTokenValid(response.getToken(), mockedUser)).isTrue();
        }

        @Nested
        class Email {

            @Test
            public void emptyEmailLeadsTo400() throws Exception {
                //Arrange
                var request = SignInRequest.builder()
                        .email("")
                        .password("pas3w@rD")
                        .build();

                //Act
                var result = mockMvc.perform(post("/v1/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                );

                //Assert
                result.andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.message")
                                .value("Email must be valid in format, example: tomas@gmail.com"));
            }

            @Test
            public void invalidEmailLeadsTo400() throws Exception {
                //Arrange
                var request = SignInRequest.builder()
                        .email("invalid-email.")
                        .password("pas3w@rD")
                        .build();

                //Act
                var result = mockMvc.perform(post("/v1/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                );

                //Assert
                result.andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.message")
                                .value("Email must be valid in format, example: tomas@gmail.com"));
            }
        }
    }

    private User getMockedUser() {
        var mockedUserId = getMockedUserId();
        return userRepository.findById(mockedUserId).orElseThrow();
    }
}
