package com.example.picturelsb;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.Map;

@RestController
public class PictureController {

    @CrossOrigin
    @PostMapping("/upload")
    public Map<String, Integer> upload(@RequestBody Map<String, String> payload) {
        String base64Image = payload.get("image");
        if (base64Image == null) {
            throw new IllegalArgumentException("No image data found!");
        }
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        BitMapImage bitMapImage = new BitMapImage(imageBytes);
        int maxEmbedLength = bitMapImage.getMaxCapacity();
        return Map.of("maxEmbedLength", maxEmbedLength);
    }

    @CrossOrigin
    @PostMapping("/embed")
    public Map<String, String> embed(@RequestBody Map<String, String> payload) {
        String base64Image = payload.get("image");
        String message = payload.get("message");
        if (base64Image == null || message == null) {
            throw new IllegalArgumentException("Image or message data not found!");
        }

        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        BitMapImage bitMapImage = new BitMapImage(imageBytes);
        bitMapImage.embed(message);

        String embeddedImageBase64 = Base64.getEncoder().encodeToString(bitMapImage.getImageBytes());
        return Map.of("image", embeddedImageBase64);
    }

    @CrossOrigin
    @PostMapping("/extract")
    public Map<String, String> extract(@RequestBody Map<String, String> payload) {
        System.out.println("coming");
        String base64Image = payload.get("image");
        if (base64Image == null) {
            throw new IllegalArgumentException("No image data found!");
        }

        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        BitMapImage bitMapImage = new BitMapImage(imageBytes);
        String extractedMessage = bitMapImage.extract();
        System.out.println(extractedMessage);
        return Map.of("extractedMessage", extractedMessage);
    }

    @CrossOrigin
    @PostMapping("/addNoise")
    public Map<String, String> addNoise(@RequestBody Map<String, String> payload) {
        String base64Image = payload.get("image");
        if (base64Image == null) {
            throw new IllegalArgumentException("No image data found!");
        }

        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        BitMapImage bitMapImage = new BitMapImage(imageBytes);
        bitMapImage.addGaussianNoise(0, 10); // 你可以根据需求调整噪声的均值和标准差

        String noisyImageBase64 = Base64.getEncoder().encodeToString(bitMapImage.getImageBytes());
        return Map.of("image", noisyImageBase64);
    }

    @CrossOrigin
    @PostMapping("/calculateAccuracy")
    public Map<String, Double> calculateAccuracy(@RequestBody Map<String, String> payload) {
        String originalMessage = payload.get("originalMessage");
        String extractedMessage = payload.get("extractedMessage");
        if (originalMessage == null || extractedMessage == null) {
            throw new IllegalArgumentException("Original message or extracted message data not found!");
        }

        BitMapImage bitMapImage = new BitMapImage(); // 你可以创建一个虚拟的BitMapImage对象来调用方法
        double accuracy = bitMapImage.calculateAccuracy(originalMessage, extractedMessage);

        return Map.of("accuracy", accuracy);
    }

    @CrossOrigin
    @PostMapping("/embedWithCypher")
    public Map<String, String> embedWithCypher(@RequestBody Map<String, String> payload) {
        String base64Image = payload.get("image");
        String message = payload.get("message");
        String cypher = payload.get("cypher");
        if (base64Image == null || message == null || cypher == null) {
            throw new IllegalArgumentException("Image, message, or cypher data not found!");
        }

        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        BitMapImage bitMapImage = new BitMapImage(imageBytes);
        bitMapImage.embedWithCypher(message, cypher);

        String embeddedImageBase64 = Base64.getEncoder().encodeToString(bitMapImage.getImageBytes());
        System.out.println("back");
        return Map.of("image", embeddedImageBase64);
    }

    @CrossOrigin
    @PostMapping("/extractWithCypher")
    public Map<String, String> extractWithCypher(@RequestBody Map<String, String> payload) {
        String base64Image = payload.get("image");
        String cypher = payload.get("cypher");
        if (base64Image == null || cypher == null) {
            throw new IllegalArgumentException("Image or cypher data not found!");
        }

        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        BitMapImage bitMapImage = new BitMapImage(imageBytes);
        String extractedMessage = bitMapImage.extractWithCypher(cypher);
        return Map.of("extractedMessage", extractedMessage);
    }
}
