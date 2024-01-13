package com.github.straightth.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.straightth.MockMvcAbstractTest;
import com.github.straightth.authentication.WithUserMock;
import com.github.straightth.domain.User;
import com.github.straightth.dto.request.UpdateUserHimselfRequest;
import com.github.straightth.repository.UserRepository;
import java.util.UUID;
import java.util.function.Consumer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

public class UserControllerTest extends MockMvcAbstractTest {

    private static final String RANDOM_UUID = UUID.randomUUID().toString();

    @Autowired
    private UserRepository userRepository;

    @Nested
    class GetUserById {

        @Test
        @WithUserMock
        public void getUserById() throws Exception {
            //Arrange
            var expectedUserId = createUser(u -> u.setUsername("Expected user"));
            createUser(u -> u.setUsername("Another user"));

            //Act
            var result = mockMvc.perform(get("/v1/user/{userId}", expectedUserId));

            //Assert
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(expectedUserId))
                    .andExpect(jsonPath("$.username").value("Expected user"));
        }

        @Test
        @WithUserMock
        public void nonexistentUserLeadsTo404() throws Exception {
            //Arrange
            createUser(u -> u.setUsername("User 1"));
            createUser(u -> u.setUsername("User 2"));

            //Act
            var result = mockMvc.perform(get("/v1/user/{userId}", RANDOM_UUID));

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
            var result = mockMvc.perform(get("/v1/user/self"));

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
                    .telegram("newTelegram")
                    .role("newRole")
                    .phoneNumber("+1")
                    .build();

            var expectedUserId = getMockedUserId();
            var expectedUser = User.builder()
                    .id(expectedUserId)
                    .email("explicit@email.com")
                    .username("newUsername")
                    .password("explicitPassword")
                    .telegram("newTelegram")
                    .role("newRole")
                    .phoneNumber("+1")
                    .build();

            //Act
            var result = mockMvc.perform(put("/v1/user/self")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            //Assert
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("newUsername"))
                    .andExpect(jsonPath("$.telegram").value("newTelegram"))
                    .andExpect(jsonPath("$.role").value("newRole"))
                    .andExpect(jsonPath("$.phoneNumber").value("+1"));

            var user = userRepository.findByEmail("explicit@email.com").orElseThrow();
            Assertions.assertThat(user)
                    .usingRecursiveComparison()
                    .isEqualTo(expectedUser);
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
                    .telegram(null)
                    .role(null)
                    .phoneNumber(null)
                    .build();

            //Act
            var result = mockMvc.perform(put("/v1/user/self")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            //Assert
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("explicitUsername"))
                    .andExpect(jsonPath("$.telegram").isEmpty())
                    .andExpect(jsonPath("$.role").isEmpty())
                    .andExpect(jsonPath("$.phoneNumber").isEmpty());

            var user = userRepository.findByEmail("explicit@email.com").orElseThrow();
            Assertions.assertThat(user)
                    .usingRecursiveComparison()
                    .isEqualTo(expectedUser);
        }
    }

    private String createUser(Consumer<User> preconfigure) {
        var user = User.builder()
                .username("Test username")
                .email("test@email.com")
                .build();
        preconfigure.accept(user);
        return userRepository.save(user).getId();
    }
}
