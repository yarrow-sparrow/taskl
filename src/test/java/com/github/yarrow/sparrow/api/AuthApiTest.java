package com.github.yarrow.sparrow.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.yarrow.sparrow.MockMvcAbstractTest;
import com.github.yarrow.sparrow.authentication.WithUserMock;
import com.github.yarrow.sparrow.domain.User;
import com.github.yarrow.sparrow.dto.response.SignInResponseDeprecated;
import com.github.yarrow.sparrow.generated.model.SignInRequest;
import com.github.yarrow.sparrow.generated.model.SignUpRequest;
import com.github.yarrow.sparrow.repository.UserRepository;
import com.github.yarrow.sparrow.service.authentication.JwtService;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * TODO: think about tests after API generation
 */
public class AuthApiTest extends MockMvcAbstractTest {

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
            var request = new SignUpRequest()
                    .username("User")
                    .email("email@email.com")
                    .password("pas3w@rD");

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
            var request = new SignUpRequest()
                    .username("Username")
                    .email("explicit@email.com")
                    .password("pas3w@rD");

            //Act
            var result = mockMvc.perform(post("/v1/auth/sign-up")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            );

            //Assert
            result.andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message")
                            .value("Email already in use by an existing account"));
        }

        @Nested
        class Validation {

            @Nested
            class Username {

                @Test
                public void emptyUsernameLeadsTo400() throws Exception {
                    //Arrange
                    var request = new SignUpRequest()
                            .username("")
                            .email("email@email.com")
                            .password("pas3w@rD");

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
                    var request = new SignUpRequest()
                            .username("*")
                            .email("email@email.com")
                            .password("pas3w@rD");

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
                    var request = new SignUpRequest()
                            .username(StringUtils.repeat("*", 30))
                            .email("email@email.com")
                            .password("pas3w@rD");

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
                    var request = new SignUpRequest()
                            .username(StringUtils.repeat("*", 31))
                            .email("email@email.com")
                            .password("pas3w@rD");

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
                    var request = new SignUpRequest()
                            .username("Username")
                            .email("")
                            .password("pas3w@rD");

                    //Act
                    var result = mockMvc.perform(post("/v1/auth/sign-up")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Email must follow pattern elizabeth@gmail.com"));
                }

                @Test
                public void invalidEmailLeadsTo400() throws Exception {
                    //Arrange
                    var request = new SignUpRequest()
                            .username("Username")
                            .email("invalid-email.")
                            .password("pas3w@rD");

                    //Act
                    var result = mockMvc.perform(post("/v1/auth/sign-up")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Email must follow pattern elizabeth@gmail.com"));
                }
            }

            @Nested
            class Password {

                @Test
                public void unsecurePasswordLeadsTo400() throws Exception {
                    //Arrange
                    var request = new SignUpRequest()
                            .username("Username")
                            .email("email@email.com")
                            .password("password");

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
            var request = new SignInRequest()
                    .email("explicit@email.com")
                    .password("pas3w@rD");

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
                    SignInResponseDeprecated.class
            );
            Assertions.assertThat(jwtService.isTokenValid(response.getToken(), mockedUser)).isTrue();
        }

        @Nested
        class Validation {
            @Nested
            class Email {

                @Test
                public void emptyEmailLeadsTo400() throws Exception {
                    //Arrange
                    var request = new SignInRequest()
                            .email("")
                            .password("pas3w@rD");

                    //Act
                    var result = mockMvc.perform(post("/v1/auth/sign-in")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Email must follow pattern elizabeth@gmail.com"));
                }

                @Test
                public void invalidEmailLeadsTo400() throws Exception {
                    //Arrange
                    var request = new SignInRequest()
                            .email("invalid-email.")
                            .password("pas3w@rD");

                    //Act
                    var result = mockMvc.perform(post("/v1/auth/sign-in")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Email must follow pattern elizabeth@gmail.com"));
                }
            }
        }
    }

    private User getMockedUser() {
        var mockedUserId = getMockedUserId();
        return userRepository.findById(mockedUserId).orElseThrow();
    }
}
