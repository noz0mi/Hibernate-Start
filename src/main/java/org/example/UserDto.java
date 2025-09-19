package org.example;

import lombok.Data;

@Data
public class UserDto {

    private Long id;
    private String name;
    private String email;

    public UserDto() {
    }

    public UserDto(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
