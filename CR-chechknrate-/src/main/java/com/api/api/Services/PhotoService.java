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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PhotoService {

    @Autowired
    private GridFSBucket gridFSBucket;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;


    @Autowired
    private PhotoRepo photoRepository;

    private final PhotoRepo photoRepo;
    public PhotoService(PhotoRepo photoRepo) {
        this.photoRepo = photoRepo;
    }
    public List<Photo> getUserPhotos(String userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));


        List<Photo> photos = user.getPhotos();
        return photos != null ? photos : new ArrayList<>();
    }



    public List<String> getPhotosByUser(String userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return user.getPhotos().stream()
                .map(Photo::getId)
                .collect(Collectors.toList());
    }

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

        Optional<Photo> photoOptional = photoRepository.findById(id);
        photoOptional.ifPresent(photo -> photoRepository.delete(photo));

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

        Rating newRating = new Rating();
        newRating.setUserId(userId);
        newRating.setScore(score);

        photo.getRatings().add(newRating);
        photo.setAverageScore(photo.calculateAverageScore());
        photoRepo.save(photo);

        User owner = userRepo.findById(photo.getOwner().getId())
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));

        if (owner.getPhotos() == null) {
            owner.setPhotos(new ArrayList<>());
        }

        userRepo.save(owner);
    }
    public String uploadPhoto(MultipartFile file, String ownerId) throws IOException {

        User owner = userRepo.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));
        System.out.println("11");

        ObjectId fileId = gridFSBucket.uploadFromStream(file.getOriginalFilename(), file.getInputStream());
        System.out.println("12");

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


        owner.getPhotos().add(photo);
        System.out.println("17");
        userRepo.save(owner);
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
