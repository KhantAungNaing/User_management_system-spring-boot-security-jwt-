package com.app.usermgmtsys.dto;

import com.app.usermgmtsys.entity.Users;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestResponse {

    private int statusCode;
    private String error;
    private String message;
    private String token;
    private String refreshToken;
    private String expiresIn;
    private String email;
    private String name;
    private String city;
    private String role;
    private String password;
    private Users user;
    private List<Users> users;

}
