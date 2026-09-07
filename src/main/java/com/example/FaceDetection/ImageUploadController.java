package com.example.FaceDetection;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/faces")
public class ImageUploadController {

    private final FaceExtractionService faceExtractionService;


    public ImageUploadController(FaceExtractionService faceExtractionService) {
        this.faceExtractionService = faceExtractionService;
    }

    @PostMapping(
            value = "/extract",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public ResponseEntity<String> extractFace(
            @RequestParam("image") MultipartFile image) throws IOException {

        String faceImage = faceExtractionService.extractAndStoreFace(image);

        System.out.println("Face image stored with ID: " + faceImage);
        return ResponseEntity.ok(faceImage);
    }
    @GetMapping(
            value = "/{id}",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public ResponseEntity<byte[]> getFace(
            @PathVariable String id
    ) {

        byte[] image = faceExtractionService.getImage(id);

        return ResponseEntity.ok(image);
    }
}
