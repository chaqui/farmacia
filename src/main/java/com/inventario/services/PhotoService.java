package com.inventario.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;

@Service
public class PhotoService {
    private final Path rootLocation;

    public PhotoService(@Value("${photos.storage.location:uploads/photos}") String storageLocation) {
        this.rootLocation = Paths.get(storageLocation).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not create storage directory", e);
        }
    }

    public String store(MultipartFile file) throws IOException {
        String original = StringUtils.cleanPath(file.getOriginalFilename());
        if (file.isEmpty() || original == null || original.isBlank()) {
            throw new IOException("Fichero vacío o nombre inválido");
        }

        String ext = "";
        int i = original.lastIndexOf('.');
        if (i >= 0) {
            ext = original.substring(i + 1).toLowerCase();
        }
        if (!Arrays.asList("jpg", "jpeg", "png").contains(ext)) {
            throw new IOException("Sólo se permiten imágenes JPG o PNG");
        }

        String filename = UUID.randomUUID().toString() + "." + ext;
        Path destination = this.rootLocation.resolve(filename).normalize();
        try (InputStream input = file.getInputStream()) {
            Files.copy(input, destination, StandardCopyOption.REPLACE_EXISTING);
        }
        return filename; // retornamos el nombre relativo dentro del storage
    }

    public Resource loadAsResource(String filename) throws MalformedURLException {
        Path file = this.rootLocation.resolve(filename).normalize();
        UrlResource resource = new UrlResource(file.toUri());
        if (resource.exists() && resource.isReadable()) {
            return resource;
        }
        throw new MalformedURLException("File not found: " + filename);
    }
}
