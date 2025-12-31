package com.arklok.nutra.helpers;

import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Helper class to load images with proper error handling
 */
public class ImageLoader {

    private static final Logger log = LoggerFactory.getLogger(ImageLoader.class);

    /**
     * Supported image formats by JavaFX
     */
    private static final String[] SUPPORTED_FORMATS = {".png", ".jpg", ".jpeg", ".gif", ".bmp"};

    /**
     * Load an image from a file path
     * @param filePath The path to the image file
     * @return The loaded Image, or null if loading failed
     */
    public static Image loadImage(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            log.warn("Image path is null or empty");
            return null;
        }

        File imageFile = new File(filePath);
        
        if (!imageFile.exists()) {
            log.error("Image file does not exist: {}", imageFile.getAbsolutePath());
            return null;
        }

        // Check if file format is supported
        String fileName = imageFile.getName().toLowerCase();
        boolean isSupported = false;
        for (String format : SUPPORTED_FORMATS) {
            if (fileName.endsWith(format)) {
                isSupported = true;
                break;
            }
        }

        if (!isSupported) {
            log.error("Unsupported image format: {}. Supported formats are: PNG, JPG, JPEG, GIF, BMP", fileName);
            log.error("WebP format is NOT supported by JavaFX. Please convert your image to PNG or JPG.");
            return null;
        }

        try {
            log.info("Loading image from: {}", imageFile.getAbsolutePath());
            
            // Try loading with FileInputStream first for better error handling
            try (InputStream is = new FileInputStream(imageFile)) {
                Image image = new Image(is);
                
                if (image.isError()) {
                    Exception ex = image.getException();
                    log.error("Error loading image - Image is in error state. Exception: {}", 
                            ex != null ? ex.getMessage() : "Unknown error");
                    if (ex != null) {
                        log.error("Stack trace:", ex);
                    }
                    return null;
                }
                
                log.info("Image loaded successfully: {} ({}x{})", 
                        imageFile.getName(), 
                        (int) image.getWidth(), 
                        (int) image.getHeight());
                return image;
            }
        } catch (Exception e) {
            log.error("Exception while loading image from {}: {}", imageFile.getAbsolutePath(), e.getMessage(), e);
            return null;
        }
    }

    /**
     * Check if a file format is supported
     * @param filePath The path to check
     * @return true if the format is supported, false otherwise
     */
    public static boolean isSupportedFormat(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }

        String fileName = filePath.toLowerCase();
        for (String format : SUPPORTED_FORMATS) {
            if (fileName.endsWith(format)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get a user-friendly error message for unsupported formats
     * @return Error message
     */
    public static String getUnsupportedFormatMessage() {
        return """
                El formato de imagen no es compatible.
                
                Formatos soportados: PNG, JPG, JPEG, GIF, BMP
                
                Nota: El formato WebP NO es compatible con JavaFX.
                Por favor, convierte tu imagen a PNG o JPG.""";
    }
}

