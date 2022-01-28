package com.awsimageupload.filestore;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

@Service
public class FileStore {
    private final AmazonS3 s3;

    @Autowired
    public FileStore(AmazonS3 s3) {
        this.s3 = s3;
    }

    /**
     * create
     */
    public void save(String filePath,
                     String fileName,
                     InputStream inputStream,
                     Optional<Map<String, String >> optionalMetadata){

        ObjectMetadata metadata = new ObjectMetadata();

        optionalMetadata.ifPresent(map ->{
            if (!map.isEmpty()){
                map.forEach(metadata::addUserMetadata);
            }
        });

        try {
            s3.putObject(filePath, fileName, inputStream, metadata);
        } catch (AmazonServiceException e){
            throw new IllegalStateException("Failed to save to bucket", e);
        }

    }

    public byte[] download(String path, String key) {
        try {
            S3Object object = s3.getObject(path, key);
            S3ObjectInputStream inputStream = object.getObjectContent();

            return IOUtils.toByteArray(inputStream);

        } catch (AmazonServiceException | IOException e){
            throw new IllegalStateException("Failed to download file from s3 bucket", e);
        }
    }
}
