package com.github.straightth.service.user;

import com.github.straightth.MockMvcAbstractTest;
import com.github.straightth.domain.User;
import com.github.straightth.repository.UserRepository;
import com.github.straightth.util.TestEntityFactory;
import java.util.function.Consumer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserAccessServiceTest extends MockMvcAbstractTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAccessService userAccessService;

    @Test
    public void userIsReturned() {
        //Arrange
        var expectedUserId = createUser(u -> {
            u.setUsername("Expected user");
            u.setEmail("expected@email.com");
            u.setPassword("salted-password");
        });

        var expectedUser = User.builder()
                .id(expectedUserId)
                .username("Expected user")
                .email("expected@email.com")
                .password("salted-password")
                .build();

        //Act
        var user = userAccessService.getPresentOrThrow(expectedUserId);

        //Assert
        Assertions.assertThat(user).isEqualTo(expectedUser);
    }

    private String createUser(Consumer<User> preconfigure) {
        var user = TestEntityFactory.createUser();
        preconfigure.accept(user);
        return userRepository.save(user).getId();
    }
}
