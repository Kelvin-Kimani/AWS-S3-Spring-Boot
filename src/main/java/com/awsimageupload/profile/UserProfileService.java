package com.awsimageupload.profile;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.awsimageupload.bucket.BucketName;
import com.awsimageupload.datastore.FakeUserProfileDataStore;
import com.awsimageupload.filestore.FileStore;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.apache.http.entity.ContentType.*;

@Service
public class UserProfileService {

    private final FakeUserProfileDataStore fakeUserProfileDataStore;
    private final FileStore fileStore;

    @Autowired
    public UserProfileService(FakeUserProfileDataStore fakeUserProfileDataStore, FileStore fileStore) {
        this.fakeUserProfileDataStore = fakeUserProfileDataStore;
        this.fileStore = fileStore;
    }


    public List<UserProfile> getUserProfiles(){
        return fakeUserProfileDataStore.getUserProfiles();
    }

    public void uploadUserImage(UUID userProfileId, MultipartFile file) {
        //1. Check if image is not empty
        if (file.isEmpty()){
            throw new IllegalStateException("File is empty, " + file.getSize());
        }

        //2. If file is an image
        if (!Arrays.asList(
                IMAGE_JPEG.getMimeType(),
                IMAGE_PNG.getMimeType(),
                IMAGE_GIF.getMimeType()).contains(file.getContentType())){
            throw new IllegalStateException("File must be an image " + file.getContentType());
        }

        //3. The user exists in our database
        UserProfile user = fakeUserProfileDataStore.getUserProfiles()
                .stream()
                .filter(userProfile -> userProfile.getUserProfileId().equals(userProfileId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("User with id %s does not exist", userProfileId)));

        //4. Grab some metadata from the file, if any
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));

        //5. Store the image in s3 and update database (userProfileLink) with s3 image link
        String path = String.format("%s/%s", BucketName.PROFILE_IMAGE.getBucketName(), user.getUserProfileId());
        String fileName = String.format("%s-%s", file.getOriginalFilename(), UUID.randomUUID());

        try {
            fileStore.save(path, fileName, file.getInputStream(), Optional.of(metadata));
            user.setUserProfileImageLink(fileName);
        } catch (IOException e) {
            throw new IllegalStateException("Error" + e);
        }
    }

    public byte[] downloadUserProfileImage(UUID userProfileId) {
        //1. The user exists in our database
        UserProfile user = fakeUserProfileDataStore.getUserProfiles()
                .stream()
                .filter(userProfile -> userProfile.getUserProfileId().equals(userProfileId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("User with id %s does not exist", userProfileId)));

        String path = String.format("%s/%s",
                BucketName.PROFILE_IMAGE.getBucketName(),
                user.getUserProfileId());

        return user.getUserProfileImageLink()
                .map(key -> fileStore.download(path, key))
                .orElse(new byte[0]);
    }
}
