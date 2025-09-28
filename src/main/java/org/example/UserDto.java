package org.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

@Data
@Relation(collectionRelation = "users", itemRelation = "user")
public class UserDto {

    @JsonProperty("id")
    private Long id;
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    private String name;
    private String email;

    public UserDto() {
    }

    public UserDto(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
