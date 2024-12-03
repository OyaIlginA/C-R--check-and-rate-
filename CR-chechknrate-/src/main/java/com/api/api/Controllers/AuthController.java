package com.api.api.Controllers;

import com.api.api.Repos.UserRepo;
import com.api.api.Entities.User;
import com.api.api.Requests.LoginRequest;
import com.api.api.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
    public ResponseEntity<String> loginUser(@RequestBody LoginRequest loginRequest) {
        User user=userService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok("Login successful, API Key: " + user.getApikey());
    }
    @Autowired
    private UserRepo userRepo;
    @PostMapping("/update")
    public ResponseEntity<String> updateUser(
            @RequestParam("api") String apikey,
            @RequestParam("uname") String uname,
            @RequestBody User user) {

        // Veritabanındaki kullanıcıyı kontrol etmek için userRepo'yu kullanıyoruz
        Optional<User> existingUser = userRepo.findByUsername(uname);

        if (existingUser.isPresent()) {
            User dbUser = existingUser.get();

            // API Key doğrulaması
            if (apikey.equals(dbUser.getApikey())) {
                // Kullanıcı bilgilerini güncelle
                dbUser.setUsername(user.getUsername());
                dbUser.setPassword(user.getPassword()); // Şifreyi hashle
                userRepo.save(dbUser); // Güncellenmiş kullanıcıyı kaydet

                return ResponseEntity.ok("User information updated successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid API Key.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }

}