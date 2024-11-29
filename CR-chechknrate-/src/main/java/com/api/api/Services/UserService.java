package com.api.api.Services;

import com.api.api.Entities.User;
import com.api.api.Repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    public UserService(UserRepo userRepo){
        super();
        this.userRepo = userRepo;
    }


    // Yeni kullanıcı kaydet
    public User registerUser(String username, String password) {
        // Kullanıcı zaten var mı kontrol et
        Optional<User> existingUser = userRepo.findByUsername(username);
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Username already exists!");
        }

        // Yeni kullanıcı oluştur ve kaydet
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password); // Şifre düz metin olarak kaydediliyor
        return userRepo.save(newUser);
    }

    // Giriş yap
    public User loginUser(String username, String password) {
        Optional<User> user = userRepo.findByUsername(username);
        if (user.isPresent()) {
            if (user.get().getPassword().equals(password)) {
                return user.get(); // Giriş başarılı
            } else {
                throw new IllegalArgumentException("Invalid password!");
            }
        } else {
            throw new IllegalArgumentException("User not found!");
        }
    }

    // Kullanıcıyı kullanıcı adına göre getir
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


}