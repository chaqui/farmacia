package com.inventario.controller;

import com.inventario.services.PhotoService;

import lombok.extern.log4j.Log4j2;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.inventario.security.ValidateToken;
import com.inventario.security.SystemRoles;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;

@RestController
@RequestMapping("/photos")
@Log4j2
public class PhotoController {
    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
        log.warn("✓✓✓ PhotoController INICIALIZADO ✓✓✓");
    }

    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    @ValidateToken(roles = {SystemRoles.VENDEDOR, SystemRoles.COMPRAS, SystemRoles.ADMINISTRADOR})
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        try {
            String storedPath = photoService.store(file);
            return ResponseEntity.ok(Collections.singletonMap("path", storedPath));
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", ex.getMessage()));
        }
    }

    @GetMapping("/download")
    @ValidateToken(roles = {SystemRoles.VENDEDOR, SystemRoles.CAJA, SystemRoles.COMPRAS, SystemRoles.ADMINISTRADOR})
    public ResponseEntity<Resource> download(@RequestParam(name = "path", required = false) String path) {
        System.out.println("@@@ ENDPOINT DOWNLOAD LLAMADO - path: " + path);
        log.warn("@@@ ENDPOINT DOWNLOAD LLAMADO - path: " + path);
        log.debug("=== DOWNLOAD ENDPOINT LLAMADO ===");
        log.info("Descargando foto desde path: " + path);
        try {
            Resource resource = photoService.loadAsResource(path);
            String contentType = null;
            try {
                contentType = Files.probeContentType(resource.getFile().toPath());
            } catch (Exception ignored) {
            }
            if (contentType == null) contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
