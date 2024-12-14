package com.api.api.Services;

import com.api.api.Entities.Photo;
import com.api.api.Entities.Rating;
import com.api.api.Entities.User;
import com.api.api.Repos.PhotoRepo;
import com.api.api.Repos.UserRepo;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PhotoService {

    @Autowired
    private GridFSBucket gridFSBucket;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;

    private final PhotoRepo photoRepo;
    public PhotoService(PhotoRepo photoRepo) {
        this.photoRepo = photoRepo;
    }
    public List<Photo> getUserPhotos(String userId) {
// Kullanıcıyı bul
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Kullanıcının fotoğraf listesini döndür
        List<Photo> photos = user.getPhotos();
        return photos != null ? photos : new ArrayList<>(); // Null güvenliği
    }



    public List<String> getPhotosByUser(String userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Fotoğraf ID'lerini döndür
        return user.getPhotos().stream()
                .map(Photo::getId) // Fotoğraf ID'lerini al
                .collect(Collectors.toList());
    }


    //public String uploadPhoto(MultipartFile file, String username) throws IOException {
    //    ObjectId fileId = gridFSBucket.uploadFromStream(file.getOriginalFilename(), file.getInputStream());
    //
    //    // Fotoğrafı kullanıcıya ekle
    //   // String photoId = fileId.toString();
    //    //userService.addPhotoToUser(username, photoId);
    //    return fileId.toString();
    //}

    public byte[] getPhoto(String id) throws IOException {
        GridFSFile gridFSFile = gridFSBucket.find(new org.bson.Document("_id", new ObjectId(id))).first();
        if (gridFSFile == null) {
            throw new IllegalArgumentException("Photo not found");
        }

        GridFSDownloadStream downloadStream = gridFSBucket.openDownloadStream(new ObjectId(id));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = downloadStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        downloadStream.close();
        return outputStream.toByteArray();
    }

    public void deletePhoto(String id) {
        gridFSBucket.delete(new ObjectId(id));
    }

    public List<String> getAllPhotos() {
        List<String> photoIds = new ArrayList<>();
        gridFSBucket.find().forEach(gridFSFile -> photoIds.add(gridFSFile.getObjectId().toString()));

        return photoIds;
    }
/*
    public List<String> getAllPhotos() {
        List<String> photoUrls = new ArrayList<>();
        gridFSBucket.find().forEach(gridFSFile -> {
            String photoId = gridFSFile.getObjectId().toString();
            String photoUrl = "/api/photo/" + photoId;  // Fotoğraf URL'sini oluşturuyoruz
            photoUrls.add(photoUrl);
        });
        return photoUrls;
    }
*/

    //rating işlemleri ------------------------------------------------------------------------------------------------

    public void ratePhoto(String photoId, String userId, int score) {
        Photo photo = photoRepo.findById(photoId)
                .orElseThrow(() -> new IllegalArgumentException("Photo not found"));

        if (photo.getRatings().stream().anyMatch(r -> r.getUserId().equals(userId))) {
            throw new IllegalArgumentException("User has already rated this photo");
        }

        // Yeni bir puan ekle
        Rating newRating = new Rating();
        newRating.setUserId(userId);
        newRating.setScore(score);

        photo.getRatings().add(newRating);
        photo.setAverageScore(photo.calculateAverageScore());
        photoRepo.save(photo);

        // Kullanıcıyı güncelle
        User owner = userRepo.findById(photo.getOwner().getId())
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));

        // Null kontrolü yapın (önlem amaçlı)
        if (owner.getPhotos() == null) {
            owner.setPhotos(new ArrayList<>());
        }

        userRepo.save(owner);
    }
    public String uploadPhoto(MultipartFile file, String ownerId) throws IOException {
        // Kullanıcıyı kontrol et
        User owner = userRepo.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));
        System.out.println("11");
        // Fotoğrafı GridFS'e yükle
        ObjectId fileId = gridFSBucket.uploadFromStream(file.getOriginalFilename(), file.getInputStream());
        System.out.println("12");
        // Metadata oluştur ve kaydet
        Photo photo = new Photo();
        photo.setId(fileId.toString());
        System.out.println("13");
        photo.setName(file.getOriginalFilename());
        System.out.println("14");
        photo.setOwner(owner);
        System.out.println("15");
        photo.setRatings(new ArrayList<>());
        photo.setAverageScore(0.0);

        photoRepo.save(photo);
        System.out.println("16");

        // Kullanıcının `photos` listesine yeni fotoğrafı ekle
        owner.getPhotos().add(photo);
        System.out.println("17");
        userRepo.save(owner); // Kullanıcıyı güncelle
        System.out.println("18");

        return fileId.toString();
    }

    public Double getPhotoAverageScore(String photoId) {
        System.out.println("*");
        Photo photo = photoRepo.findById(photoId)
                .orElseThrow(() -> new IllegalArgumentException("Photo not found"));
        System.out.println("8");

        return photo.getAverageScore();

    }
}
