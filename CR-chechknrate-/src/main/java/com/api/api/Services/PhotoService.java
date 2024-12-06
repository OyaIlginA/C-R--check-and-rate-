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
        List<Photo> user = photoRepo.findByUserId(userId);
        return user;
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

        // Fotoğrafı GridFS'e yükle
        ObjectId fileId = gridFSBucket.uploadFromStream(file.getOriginalFilename(), file.getInputStream());

        // Metadata oluştur ve kaydet
        Photo photo = new Photo();
        photo.setId(fileId.toString());
        photo.setName(file.getOriginalFilename());
        photo.setOwner(owner);
        photo.setRatings(new ArrayList<>());
        photo.setAverageScore(0.0);

        photoRepo.save(photo);

        // Kullanıcının `photos` listesine yeni fotoğrafı ekle
        owner.getPhotos().add(photo);
        userRepo.save(owner); // Kullanıcıyı güncelle

        return fileId.toString();
    }

    public Double getPhotoAverageScore(String photoId) {
        Photo photo = photoRepo.findById(photoId)
                .orElseThrow(() -> new IllegalArgumentException("Photo not found"));

        return photo.getAverageScore();
    }
}
