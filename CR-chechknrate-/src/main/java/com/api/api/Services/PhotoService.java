package com.api.api.Services;

import com.api.api.Entities.Photo;
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

    public String uploadPhoto(MultipartFile file, String username) throws IOException {
        ObjectId fileId = gridFSBucket.uploadFromStream(file.getOriginalFilename(), file.getInputStream());

        // Fotoğrafı kullanıcıya ekle
       // String photoId = fileId.toString();
        //userService.addPhotoToUser(username, photoId);
        return fileId.toString();
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
        gridFSBucket.delete(new ObjectId(id));
    }

    public List<String> getAllPhotos() {
        List<String> photoIds = new ArrayList<>();
        gridFSBucket.find().forEach(gridFSFile -> photoIds.add(gridFSFile.getObjectId().toString()));
        return photoIds;
    }


}
