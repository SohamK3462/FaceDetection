package com.example.FaceDetection;



import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GcsStorageService {

    private static final String BUCKET_NAME =
            "face_detection_sk";

    private final Storage storage;

    public GcsStorageService() {
        this.storage = StorageOptions
                .getDefaultInstance()
                .getService();
    }

    public String uploadImage(byte[] imageBytes) {

        String fileName =
                "faces/" + UUID.randomUUID() + ".jpg";

        BlobId blobId = BlobId.of(
                BUCKET_NAME,
                fileName
        );

        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType("image/jpeg")
                .build();

        storage.create(
                blobInfo,
                imageBytes
        );

        return "gs://" + BUCKET_NAME + "/" + fileName;
    }
}
