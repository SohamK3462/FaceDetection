package com.example.FaceDetection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import nu.pattern.OpenCV;


@SpringBootApplication
public class FaceDetectionApplication {

	public static void main(String[] args) {

		OpenCV.loadLocally();

		SpringApplication.run(
				FaceDetectionApplication.class,
				args
		);
	}
}
