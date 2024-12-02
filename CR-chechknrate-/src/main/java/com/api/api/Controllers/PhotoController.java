package com.api.api.Controllers;

import com.api.api.Services.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.api.api.Entities.Photo;
import java.util.List;
import java.io.IOException;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadPhoto(@RequestParam("file") MultipartFile file) {
        try {
            String photoId = photoService.uploadPhoto(file);
            return ResponseEntity.ok("Photo uploaded successfully. ID: " + photoId);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error uploading photo: " + e.getMessage());
        }
    }


    @GetMapping("/{userid}")
    public ResponseEntity<List<Photo>> getUserPhotos(@PathVariable String userid) {
        return ResponseEntity.ok(photoService.getUserPhotos(userid));
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getPhoto(@PathVariable String id) {
        try {
            byte[] photoData = photoService.getPhoto(id);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return ResponseEntity.ok().headers(headers).body(photoData);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(null);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePhoto(@PathVariable String id) {
        try {
            photoService.deletePhoto(id);
            return ResponseEntity.ok("Photo deleted successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("Photo not found.");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<String>> getAllPhotos() {
        try {
            List<String> photoIds = photoService.getAllPhotos();
            return ResponseEntity.ok(photoIds);
        } catch (Exception e) {

            return ResponseEntity.status(500).body(null);
        }
    }
}

