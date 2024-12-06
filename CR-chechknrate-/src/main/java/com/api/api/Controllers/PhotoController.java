package com.api.api.Controllers;

import com.api.api.Entities.Photo;
import com.api.api.Entities.User;
import com.api.api.Repos.UserRepo;
import com.api.api.Services.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {


    @Autowired
    private PhotoService photoService;

    @Autowired
    private UserRepo userRepo;

    //@PostMapping("/upload")
    //    public ResponseEntity<String> uploadPhoto(
    //            @RequestParam("file") MultipartFile file,
    //            @RequestParam("api") String apikey,
    //            @RequestParam("uname") String uname) {
    //
    //        // Kullanıcı adı üzerinden API Key doğrulaması
    //        Optional<User> existingUser = userRepo.findByUsername(uname);
    //        if (existingUser.isPresent()) {
    //            User dbUser = existingUser.get();
    //            System.out.println("3");
    //            if (apikey.equals(dbUser.getApikey())) {
    //                System.out.println("4");// API Key doğrulaması başarılı
    //                try {System.out.println("2");
    //                    // Fotoğrafı yükle
    //                    String photoId = photoService.uploadPhoto(file, dbUser.getUsername());
    //                    System.out.println("6");
    //
    //                  //  Photo photo = new Photo();
    //                    //photo.setUserId(dbUser.getId());
    //
    //                    // Fotoğrafın başarılı bir şekilde yüklendiği mesajını döndür
    //                    return ResponseEntity.ok("Photo uploaded successfully. ID: " + photoId + " User ID: " + dbUser.getId());
    //                } catch (IOException e) {
    //                    System.out.println("5");
    //                    // Fotoğraf yükleme hatası
    //                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading photo: " + e.getMessage());
    //                }
    //            } else {
    //                // API Key hatalı
    //                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid API Key.");
    //            }
    //        } else {
    //            // Kullanıcı bulunamadı
    //            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
    //        }
    //    }


    // Kullanıcının fotoğraflarını al
    @GetMapping("/user/{userid}")
    public ResponseEntity<List<Photo>> getUserPhotos(@PathVariable String userid,
                                                     @RequestParam("api") String apikey,
                                                     @RequestParam("uname") String uname) {
        // Kullanıcı adı ve API key doğrulaması
        Optional<User> existingUser = userRepo.findByUsername(uname);
        if (existingUser.isPresent()) {
            User dbUser = existingUser.get();
            if (apikey.equals(dbUser.getApikey())) {
                // API key doğrulaması başarılı
                return ResponseEntity.ok(photoService.getUserPhotos(userid));
            } else {
                // API key hatalı
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
        } else {
            // Kullanıcı bulunamadı
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Fotoğraf verisini al
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getPhoto(@PathVariable String id,
                                           @RequestParam("api") String apikey,
                                           @RequestParam("uname") String uname) {
        // Kullanıcı adı ve API key doğrulaması
        Optional<User> existingUser = userRepo.findByUsername(uname);
        if (existingUser.isPresent()) {
            User dbUser = existingUser.get();
            if (apikey.equals(dbUser.getApikey())) {
                // API key doğrulaması başarılı
                try {
                    byte[] photoData = photoService.getPhoto(id);
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.IMAGE_JPEG);
                    return ResponseEntity.ok().headers(headers).body(photoData);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                }
            } else {
                // API key hatalı
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
        } else {
            // Kullanıcı bulunamadı
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Fotoğraf silme
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePhoto(@PathVariable String id,
                                              @RequestParam("api") String apikey,
                                              @RequestParam("uname") String uname) {
        // Kullanıcı adı ve API key doğrulaması
        Optional<User> existingUser = userRepo.findByUsername(uname);
        if (existingUser.isPresent()) {
            User dbUser = existingUser.get();
            if (apikey.equals(dbUser.getApikey())) {
                // API key doğrulaması başarılı
                try {
                    photoService.deletePhoto(id);
                    return ResponseEntity.ok("Photo deleted successfully.");
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Photo not found.");
                }
            } else {
                // API key hatalı
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid API Key.");
            }
        } else {
            // Kullanıcı bulunamadı
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }

    // Tüm fotoğrafların ID'lerini al
    @GetMapping("/all")
    public ResponseEntity<List<String>> getAllPhotos(@RequestParam("api") String apikey,
                                                     @RequestParam("uname") String uname) {
        // Kullanıcı adı ve API key doğrulaması
        Optional<User> existingUser = userRepo.findByUsername(uname);
        if (existingUser.isPresent()) {
            User dbUser = existingUser.get();
            if (apikey.equals(dbUser.getApikey())) {
                // API key doğrulaması başarılı
                try {
                    List<String> photoIds = photoService.getAllPhotos();
                    return ResponseEntity.ok(photoIds);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                }
            } else {
                // API key hatalı
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
        } else {
            // Kullanıcı bulunamadı
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    //rating eklemesinden sonrası (uploadı değiştirmem gerekti)------------------------------------------------------------------------------

    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadPhoto(@RequestParam("api") String apikey,
                                                     @RequestParam("uname") String uname,
                                                    @RequestParam("file") MultipartFile file,
                                                    @RequestParam("ownerId") String ownerId) {
        // Kullanıcı adı ve API key doğrulaması
        Optional<User> existingUser = userRepo.findByUsername(uname);
        if (existingUser.isPresent()) {
            User dbUser = existingUser.get();
            if (apikey.equals(dbUser.getApikey())) {
                // API key doğrulaması başarılı
                try {
                    String photoId = photoService.uploadPhoto(file, ownerId);
                    return ResponseEntity.ok(Collections.singletonList("Photo uploaded successfully. ID: " + photoId + " User ID: " + ownerId));
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                }
            } else {
                // API key hatalı
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
        } else {
            // Kullanıcı bulunamadı
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/{photoId}/rate")
    public ResponseEntity<List<String>> ratePhoto(@PathVariable String photoId,
                                                  @RequestParam String userId,
                                                  @RequestParam int score,
                                                  @RequestParam("api") String apikey,
                                                  @RequestParam("uname") String uname) {
        // Kullanıcı adı ve API key doğrulaması
        Optional<User> existingUser = userRepo.findByUsername(uname);
        if (existingUser.isPresent()) {
            User dbUser = existingUser.get();
            if (apikey.equals(dbUser.getApikey())) {
                // API key doğrulaması başarılı
                try {
                    photoService.ratePhoto(photoId, userId, score);
                    return ResponseEntity.ok(Collections.singletonList("Photo rated successfully"));
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                }
            } else {
                // API key hatalı
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
        } else {
            // Kullanıcı bulunamadı
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/{photoId}/averageScore")
    public ResponseEntity<List<String>> getPhotoAverageScore(@PathVariable String photoId,
                                                            @RequestParam("api") String apikey,
                                                            @RequestParam("uname") String uname) {
        // Kullanıcı adı ve API key doğrulaması
        Optional<User> existingUser = userRepo.findByUsername(uname);
        if (existingUser.isPresent()) {
            User dbUser = existingUser.get();
            if (apikey.equals(dbUser.getApikey())) {
                // API key doğrulaması başarılı
                try {
                    Double averageScore = photoService.getPhotoAverageScore(photoId);
                    return ResponseEntity.ok((List<String>) Map.of("photoId", photoId, "averageScore", averageScore));
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                }
            } else {
                // API key hatalı
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
        } else {
            // Kullanıcı bulunamadı
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
