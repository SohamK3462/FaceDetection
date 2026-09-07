package com.example.FaceDetection;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FaceExtractionService {

    private final CascadeClassifier faceDetector;
    private final ImageStoreService imageStorageService;
    private final GcsStorageService gcsStorageService;
    private final DetectWebDetectionsGcs detectWebDetectionsGcs;

    public FaceExtractionService(
            ImageStoreService imageStorageService, GcsStorageService gcsStorageService, DetectWebDetectionsGcs detectWebDetectionsGcs
    ) throws IOException {
        this.imageStorageService = imageStorageService;
        this.gcsStorageService = gcsStorageService;
        this.detectWebDetectionsGcs = detectWebDetectionsGcs;

        ClassPathResource resource =
                new ClassPathResource("haarcascade_frontalface_default.xml");

        faceDetector = new CascadeClassifier(
                resource.getFile().getAbsolutePath()
        );

        if (faceDetector.empty()) {
            throw new IllegalStateException(
                    "Could not load face detector"
            );
        }
    }

    public String extractAndStoreFace(
            MultipartFile image
    ) throws IOException {

        byte[] imageBytes = image.getBytes();

        Mat inputImage = Imgcodecs.imdecode(
                new MatOfByte(imageBytes),
                Imgcodecs.IMREAD_COLOR
        );

        if (inputImage.empty()) {
            throw new IllegalArgumentException(
                    "Invalid image"
            );
        }

        MatOfRect faces = new MatOfRect();

        faceDetector.detectMultiScale(
                inputImage,
                faces
        );

        Rect[] detectedFaces = faces.toArray();

        if (detectedFaces.length == 0) {
            throw new IllegalArgumentException(
                    "No face detected"
            );
        }

        // Take first detected face
        Rect faceRect = detectedFaces[0];

        Mat face = new Mat(
                inputImage,
                faceRect
        );

        MatOfByte output = new MatOfByte();

        Imgcodecs.imencode(
                ".jpg",
                face,
                output
        );

        byte[] faceImage = output.toArray();

        // Store in MongoDB
        String faceId =imageStorageService.storeImage(
                faceImage,
                "face.jpg",
                "image/jpeg"
        );
        String gcId = gcsStorageService.uploadImage(faceImage);
        System.out.println("Face image stored in GCS with ID: " + gcId);
        DetectWebDetectionsGcs.detectWebDetectionsGcs(gcId,true);
        return faceId;
    }

    public byte[] getImage(String id) {
        return imageStorageService.getImage(id);
    }
}