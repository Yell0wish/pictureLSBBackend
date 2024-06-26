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
        String base64Image = payload.get("image");
        if (base64Image == null) {
            throw new IllegalArgumentException("No image data found!");
        }

        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        BitMapImage bitMapImage = new BitMapImage(imageBytes);
        String extractedMessage = bitMapImage.extract();

        return Map.of("extractedMessage", extractedMessage);
    }
}
