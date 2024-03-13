package com.github.yarrow.sparrow.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.yarrow.sparrow.MockMvcAbstractTest;
import com.github.yarrow.sparrow.authentication.WithUserMock;
import com.github.yarrow.sparrow.domain.User;
import com.github.yarrow.sparrow.dto.request.UpdateUserHimselfRequest;
import com.github.yarrow.sparrow.generated.model.SignUpRequest;
import com.github.yarrow.sparrow.repository.UserRepository;
import com.github.yarrow.sparrow.util.TestEntityFactory;
import java.util.function.Consumer;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserControllerTest extends MockMvcAbstractTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Nested
    class GetUserById {

        @Test
        @WithUserMock
        public void getUserById() throws Exception {
            //Arrange
            var expectedUserId = saveUser(u -> u.setUsername("Expected user"));
            saveUser(u -> u.setUsername("Another user"));

            //Act
            var result = mockMvc.perform(get("/v1/users/{userId}", expectedUserId));

            //Assert
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(expectedUserId))
                    .andExpect(jsonPath("$.username").value("Expected user"));
        }

        @Test
        @WithUserMock
        public void nonexistentUserLeadsTo404() throws Exception {
            //Arrange
            saveUser(u -> u.setUsername("User 1"));
            saveUser(u -> u.setUsername("User 2"));

            //Act
            var result = mockMvc.perform(get("/v1/users/{userId}", RANDOM_UUID));

            //Assert
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found"));
        }
    }

    @Nested
    class GetUserHimself {

        @Test
        @WithUserMock(username = "Expected username", email = "expected@email.com")
        public void getUserHimself() throws Exception {
            //Arrange
            var expectedUserId = getMockedUserId();

            //Act
            var result = mockMvc.perform(get("/v1/users/self"));

            //Assert
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(expectedUserId))
                    .andExpect(jsonPath("$.username").value("Expected username"))
                    .andExpect(jsonPath("$.email").value("expected@email.com"));
        }
    }

    @Nested
    class UpdateUser {

        @Test
        @WithUserMock(email = "explicit@email.com", encodedPassword = "explicitPassword")
        public void userIsUpdated() throws Exception {
            //Arrange
            var request = UpdateUserHimselfRequest.builder()
                    .username("newUsername")
                    .email("new@email.com")
                    .password("newpas3w@rD")
                    .phoneNumber("+1")
                    .build();

            var expectedUserId = getMockedUserId();
            var expectedUser = User.builder()
                    .id(expectedUserId)
                    .username("newUsername")
                    .email("new@email.com")
                    .phoneNumber("+1")
                    .build();

            //Act
            var result = mockMvc.perform(put("/v1/users/self")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            //Assert
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("newUsername"))
                    .andExpect(jsonPath("$.email").value("new@email.com"))
                    .andExpect(jsonPath("$.phoneNumber").value("+1"));

            var user = userRepository.findByEmail("new@email.com").orElseThrow();
            Assertions.assertThat(user)
                    .usingRecursiveComparison()
                    .ignoringFields("password")
                    .isEqualTo(expectedUser);
            Assertions.assertThat(passwordEncoder.matches("newpas3w@rD", user.getPassword()))
                    .isTrue();
        }

        @Test
        @WithUserMock(username = "explicitUsername", email = "explicit@email.com", encodedPassword = "explicitPassword")
        public void emptyUpdateMakeNoChanges() throws Exception {
            //Arrange
            var request = UpdateUserHimselfRequest.builder().build();

            var expectedUserId = getMockedUserId();
            var expectedUser = User.builder()
                    .id(expectedUserId)
                    .email("explicit@email.com")
                    .username("explicitUsername")
                    .password("explicitPassword")
                    .phoneNumber(null)
                    .build();

            //Act
            var result = mockMvc.perform(put("/v1/users/self")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            //Assert
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("explicitUsername"))
                    .andExpect(jsonPath("$.email").value("explicit@email.com"))
                    .andExpect(jsonPath("$.phoneNumber").isEmpty());

            var user = userRepository.findByEmail("explicit@email.com").orElseThrow();
            Assertions.assertThat(user)
                    .usingRecursiveComparison()
                    .isEqualTo(expectedUser);
        }

        @Test
        @WithUserMock(email = "explicit@email.com")
        public void alreadyUsedEmailLeadsTo409() throws Exception {
            //Arrange
            saveUser(u -> u.setEmail("new@email.com"));
            var request = new SignUpRequest()
                    .email("new@email.com");

            //Act
            var result = mockMvc.perform(put("/v1/users/self")
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
                @WithUserMock
                public void emptyUsernameLeadsTo400() throws Exception {
                    //Arrange
                    var request = UpdateUserHimselfRequest.builder()
                            .username("")
                            .build();

                    //Act
                    var result = mockMvc.perform(put("/v1/users/self")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Username must be between 1 and 30 characters"));
                }

                @Test
                @WithUserMock
                public void singleCharacterNameLeadsTo200() throws Exception {
                    //Arrange
                    var request = UpdateUserHimselfRequest.builder()
                            .username("*")
                            .build();

                    //Act
                    var result = mockMvc.perform(put("/v1/users/self")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isOk());
                }

                @Test
                @WithUserMock
                public void usernameEqualToLimitLeadsTo200() throws Exception {
                    //Arrange
                    var request = UpdateUserHimselfRequest.builder()
                            .username(StringUtils.repeat("*", 30))
                            .build();

                    //Act
                    var result = mockMvc.perform(put("/v1/users/self")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isOk());
                }

                @Test
                @WithUserMock
                public void usernameLongerThanLimitLeadsTo400() throws Exception {
                    //Arrange
                    var request = UpdateUserHimselfRequest.builder()
                            .username(StringUtils.repeat("*", 31))
                            .build();

                    //Act
                    var result = mockMvc.perform(put("/v1/users/self")
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
                @WithUserMock
                public void emptyEmailLeadsTo400() throws Exception {
                    //Arrange
                    var request = UpdateUserHimselfRequest.builder().email("").build();

                    //Act
                    var result = mockMvc.perform(put("/v1/users/self")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Email must follow pattern elizabeth@gmail.com"));
                }

                @Test
                @WithUserMock
                public void invalidEmailLeadsTo400() throws Exception {
                    //Arrange
                    var request = UpdateUserHimselfRequest.builder().email("invalid-email.").build();

                    //Act
                    var result = mockMvc.perform(put("/v1/users/self")
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
                @WithUserMock
                public void unsecurePasswordLeadsTo400() throws Exception {
                    //Arrange
                    var request = UpdateUserHimselfRequest.builder().password("password").build();

                    //Act
                    var result = mockMvc.perform(put("/v1/users/self")
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

    private String saveUser(Consumer<User> preconfigure) {
        var user = TestEntityFactory.createUser();
        preconfigure.accept(user);
        return userRepository.save(user).getId();
    }
}
