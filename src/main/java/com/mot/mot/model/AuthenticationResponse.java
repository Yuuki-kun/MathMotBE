package com.mot.mot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    @JsonProperty("accessToken")
    private String accessToken;

   @JsonProperty("email")
    private String email;

   @JsonProperty("roles")
    private List<Role> roles;

   @JsonProperty("userId")
   private Long userId;

}
