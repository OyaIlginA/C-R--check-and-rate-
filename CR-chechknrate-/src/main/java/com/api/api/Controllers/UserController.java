package com.api.api.Controllers;

import com.api.api.Repos.UserRepo;
import io.micrometer.common.util.internal.logging.InternalLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.api.api.Services.UserService;
import com.api.api.Entities.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }


    @GetMapping
    public ResponseEntity<?> getAllUsers(@RequestParam("api") String apikey, @RequestParam("uname") String uname) {
        Optional<User> existingUser = userRepo.findByUsername(uname);

        if (existingUser.isPresent()) {
            User dbUser = existingUser.get();

            // API Key doğrulaması
            if (apikey.equals(dbUser.getApikey())) {
                List<User> allUsers = userService.getAllUsers();
                return ResponseEntity.ok(allUsers);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid API Key.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }

    @PostMapping
    public User createUser(@RequestBody User newUser){
        return userService.saveOneUser(newUser);
    }


    @Autowired
    private UserRepo userRepo;


    @GetMapping("/{userId}")
    public ResponseEntity<?> getOneUser(
            @PathVariable String userId,
            @RequestParam("api") String apikey,
            @RequestParam("uname") String uname) {

        System.out.println("API Key (istekten): " + apikey);
        System.out.println("Kullanıcı adı (istekten): " + uname);

        Optional<User> existingUser = userRepo.findByUsername(uname);
        if (existingUser.isPresent()) {
            User dbUser = existingUser.get();
            System.out.println("Veritabanındaki kullanıcı adı: " + dbUser.getUsername());
            System.out.println("Veritabanındaki API Key: " + dbUser.getApikey());

            if (apikey.equals(dbUser.getApikey())) {
                return ResponseEntity.ok("Doğrulama başarılı: Kullanıcı bilgisi getirildi." + dbUser.getUsername() + ":username "+ dbUser.getPassword()+ ":password" );
            } else {
                System.out.println("API key uyuşmuyor.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid API Key.");
            }
        } else {
            System.out.println("Kullanıcı bulunamadı.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }


    @PostMapping("/update")
    public ResponseEntity<String> updateUser(
            @RequestParam("api") String apikey,
            @RequestParam("uname") String uname,
            @RequestBody User user) {

        // API Key kontrolü
        Optional<User> existingUser = userRepo.findByUsername(uname);
        if (existingUser.isPresent()) {
            User dbUser = existingUser.get();

            if (apikey.equals(dbUser.getApikey())) {
                // Kullanıcı bilgilerini güncelle
                dbUser.setUsername(user.getUsername());
                dbUser.setPassword(user.getPassword());
                userRepo.save(dbUser);

                return ResponseEntity.ok("User information updated successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid API Key.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteOneUser(@PathVariable String userId,@RequestParam("api") String apikey, @RequestParam("uname") String uname) {
        Optional<User> existingUser = userRepo.findByUsername(uname);

        if (existingUser.isPresent()) {
            User dbUser = existingUser.get();

            if (apikey.equals(dbUser.getApikey())) {
                userRepo.deleteById(userId);
                return ResponseEntity.ok("User deleted successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid API Key.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }

    @GetMapping("/{userId}/averageScore")
    public ResponseEntity<Object> getPhotoAverageScore(@PathVariable String userId,
                                                       @RequestParam("api") String apikey,
                                                       @RequestParam("uname") String uname) {
        Optional<User> existingUser = userRepo.findByUsername(uname);
        if (existingUser.isPresent()) {
            User dbUser = existingUser.get();
            if (apikey.equals(dbUser.getApikey())) {

                try {

                    double averageScore = userService.calculateUserAverageScore(userId);

                    return ResponseEntity.ok(Map.of(
                            "userId", userId,
                            "averageScore", averageScore
                    ));
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid userId: " + userId);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
                } catch (Exception e) {
                    System.err.println("Error while calculating average score: " + e.getMessage());
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal server error"));
                }
            } else {

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Invalid API key"));
            }
        } else {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }
    }
}
