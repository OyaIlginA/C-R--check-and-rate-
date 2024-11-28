package com.api.api.Controllers;

import com.api.api.Entities.User;
import com.api.api.Requests.LoginRequest;
import com.api.api.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user){
        userService.registerUser(user.getUsername(),user.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequest loginRequest){
        userService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok("Login succesful");
    }
}