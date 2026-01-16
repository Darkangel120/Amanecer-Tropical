package com.AmanecerTropical.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

@RestController
@RequestMapping("/api/qr")
public class QRController {

    @SuppressWarnings("null")
    @GetMapping(produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateQR(@RequestParam String text,
                                           @RequestParam(defaultValue = "300") int width,
                                           @RequestParam(defaultValue = "300") int height) throws Exception {

        // Generar QR en memoria
        BitMatrix matrix = new MultiFormatWriter()
            .encode(text, BarcodeFormat.QR_CODE, width, height);

        BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);

        // Convertir a bytes sin guardar archivo
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);

        return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_PNG)
            .body(baos.toByteArray());
    }

    @GetMapping(value = "/base64", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> generateQRBase64(@RequestParam String text,
                                                 @RequestParam(defaultValue = "300") int width,
                                                 @RequestParam(defaultValue = "300") int height) throws Exception {

        // Generar QR en memoria
        BitMatrix matrix = new MultiFormatWriter()
            .encode(text, BarcodeFormat.QR_CODE, width, height);

        BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);

        // Convertir a Base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());

        return ResponseEntity.ok("data:image/png;base64," + base64Image);
    }
}
