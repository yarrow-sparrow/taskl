package com.github.straightth.controller;

import com.github.straightth.dto.request.UpdateUserHimselfRequest;
import com.github.straightth.dto.response.UserResponse;
import com.github.straightth.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public UserResponse getUserById(@PathVariable String userId) {
        return userService.getUserById(userId);
    }

    @GetMapping("/self")
    public UserResponse getUserHimself() {
        return userService.getUserHimself();
    }

    @PutMapping("/self")
    public UserResponse updateUserHimself(@RequestBody @Valid UpdateUserHimselfRequest request) {
        return userService.updateUserHimself(request);
    }
}
