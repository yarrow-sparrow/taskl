package com.github.yarrow.sparrow.domain;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
public class User {

    @Id
    private String id;
    @NotEmpty
    private String username;
    private String email;
    private String password;
    private String phoneNumber;

    public String getInitials() {
        if (username.length() >= 2) {
            return username.substring(0, 2).toUpperCase();
        } else {
            return username.substring(0, 1).toUpperCase();
        }
    }
}
