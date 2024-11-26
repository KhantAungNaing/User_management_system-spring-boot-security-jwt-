package com.app.usermgmtsys.service.impl;

import com.app.usermgmtsys.dto.RequestResponse;
import com.app.usermgmtsys.entity.Users;
import com.app.usermgmtsys.repo.UsersRepo;
import com.app.usermgmtsys.service.JwtUtils;
import com.app.usermgmtsys.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class UserManagementServiceImpl implements UserManagementService {

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public RequestResponse register(RequestResponse registerRequest) {
        RequestResponse resp = new RequestResponse();
        try{
            Users user = new Users();
            user.setEmail(registerRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setCity(registerRequest.getCity());
            user.setRole(registerRequest.getRole());
            user.setName(registerRequest.getName());

            Users savedUser = usersRepo.save(user);
            if(savedUser.getId() > 0) {
                resp.setUser(savedUser);
                resp.setStatusCode(200);
                resp.setMessage("User registered successfully");
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    @Override
    public RequestResponse login(RequestResponse loginRequest) {
        RequestResponse resp = new RequestResponse();
        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail()
                            , loginRequest.getPassword()));
            var user = usersRepo.findByEmail(loginRequest.getEmail()).orElseThrow();
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            resp.setStatusCode(200);
            resp.setToken(jwt);
            resp.setRefreshToken(refreshToken);
            resp.setExpiresIn("24Hours");
            resp.setMessage("User logged in successfully");

        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }

        return resp;
    }

    public RequestResponse refreshToken(RequestResponse refreshRequest) {
        RequestResponse resp = new RequestResponse();
        try{
            String userEmail = jwtUtils.extractUserName(refreshRequest.getToken());
            Users user = usersRepo.findByEmail(userEmail).orElseThrow();
            if(jwtUtils.isTokenValid(refreshRequest.getToken(), user)) {
                var jwt = jwtUtils.generateToken(user);
                resp.setStatusCode(200);
                resp.setToken(jwt);
                resp.setRefreshToken(refreshRequest.getToken());
                resp.setExpiresIn("24Hours");
                resp.setMessage("User token refreshed successfully");
            }
        } catch(Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    public RequestResponse getAllUsers() {
        RequestResponse resp = new RequestResponse();
        try {
            List<Users> users = usersRepo.findAll();
            if(!users.isEmpty()) {
                resp.setUsers(users);
                resp.setStatusCode(200);
                resp.setMessage("Successfully get all users");
            } else {
                resp.setStatusCode(404);
                resp.setError("No users found");
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    public RequestResponse getUserById(Integer id) {
        RequestResponse resp = new RequestResponse();
        try {
            Users user = usersRepo.findById(id).orElseThrow(() -> new RuntimeException("No user found"));
            resp.setUser(user);
            resp.setStatusCode(200);
            resp.setMessage(String.format("Successfully get user by id: %s", id));
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    public RequestResponse deleteUser(Integer id) {
        RequestResponse resp = new RequestResponse();
        try {
            Optional<Users> user = usersRepo.findById(id);
            if(user.isPresent()) {
                usersRepo.deleteById(id);
                resp.setStatusCode(200);
                resp.setMessage("User deleted successfully");
            } else {
                resp.setStatusCode(404);
                resp.setError("No user found");
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    public RequestResponse updateUser(Integer id, Users updateUser) {
        RequestResponse resp = new RequestResponse();
        try {
            Optional<Users> user = usersRepo.findById(id);
            if(user.isPresent()) {
                Users userToUpdate = user.get();
                userToUpdate.setEmail(updateUser.getEmail());
                userToUpdate.setName(updateUser.getName());
                userToUpdate.setCity(updateUser.getCity());
                userToUpdate.setRole(updateUser.getRole());
                if(updateUser.getPassword() != null && !updateUser.getPassword().isEmpty()) {
                    userToUpdate.setPassword(passwordEncoder.encode(updateUser.getPassword()));
                }
                Users savedUser = usersRepo.save(userToUpdate);
                resp.setStatusCode(200);
                resp.setMessage("User updated successfully");
            } else {
                resp.setStatusCode(404);
                resp.setError("No user found");
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }
}
