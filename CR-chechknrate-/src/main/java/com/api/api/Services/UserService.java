package com.api.api.Services;

import com.api.api.Entities.User;
import com.api.api.Repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    public UserService(UserRepo userRepo){
        super();
        this.userRepo = userRepo;
    }


    public User registerUser(String username, String password) {

        Optional<User> existingUser = userRepo.findByUsername(username);
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Username already exists!");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password); // Şifre düz metin olarak kaydediliyor
        Random random = new Random();
        long randomNumber = 100000000000L + (long)(random.nextDouble() * 900000000000L);
        newUser.setApikey(String.valueOf(randomNumber));
        //newUser.setPhotos(new ArrayList<>());
       //yeni array list oluşturup ona bu atamayı yapmak lazımm
        return userRepo.save(newUser);
    }


    public User loginUser(String username, String password) {
        Optional<User> user = userRepo.findByUsername(username);
        if (user.isPresent()) {
            if (user.get().getPassword().equals(password)) {
                return user.get();
            } else {
                throw new IllegalArgumentException("Invalid password!");
            }
        } else {
            throw new IllegalArgumentException("User not found!");
        }
    }
    
    public User getUserByUsername(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found!"));
    }

    public List<User> getAllUsers(){
        return userRepo.findAll();
    }

    public User saveOneUser(User newUser){
        return userRepo.save(newUser);
    }

    public User getOneUser(String userId){
        return userRepo.findById(userId).orElse(null);
    }

    public User updateOneUser(String userId, User newUser){
        Optional<User> user = userRepo.findById(userId);
        if(user.isPresent()){
            User foundUser = user.get();
            foundUser.setUsername(newUser.getUsername());
            foundUser.setPassword(newUser.getPassword());
            userRepo.save(foundUser);
            return foundUser;
        }else
            return null;
    }

    public void deleteById(String userId){
        userRepo.deleteById(userId);
    }

    public Double calculateUserAverageScore(String userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return user.calculateUserAverageScore();
    }

}