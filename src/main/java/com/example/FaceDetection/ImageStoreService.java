package com.example.FaceDetection;

import com.mongodb.client.model.Filters;
import com.mongodb.client.gridfs.model.GridFSFile;
import java.io.ByteArrayOutputStream;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;

import com.mongodb.client.gridfs.model.GridFSUploadOptions;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
public class ImageStoreService {

    private final GridFSBucket gridFSBucket;

    public ImageStoreService(MongoTemplate mongoTemplate) {

        System.out.println("Mongo database: " + mongoTemplate.getDb().getName());
        System.out.println("Mongo URI: " +
                mongoTemplate.getMongoDatabaseFactory()
                        .getMongoDatabase()
                        .getName());
        this.gridFSBucket = GridFSBuckets.create(
                mongoTemplate.getDb()
        );
    }

    public String storeImage(
            byte[] image,
            String filename,
            String contentType
    ) {

        ObjectId fileId = gridFSBucket.uploadFromStream(
                filename,
                new ByteArrayInputStream(image),
                new GridFSUploadOptions()
                        .metadata(
                                new org.bson.Document()
                                        .append("contentType", contentType)
                        )
        );

        return fileId.toHexString();
    }

    public byte[] getImage(String id) {

        GridFSFile file = gridFSBucket.find(
                Filters.eq("_id", new ObjectId(id))
        ).first();

        if (file == null) {
            throw new IllegalArgumentException("Image not found: " + id);
        }

        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {

            gridFSBucket.downloadToStream(
                    file.getObjectId(),
                    output
            );

            return output.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read image", e);
        }
    }
}
