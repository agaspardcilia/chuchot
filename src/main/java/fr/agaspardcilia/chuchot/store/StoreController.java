package fr.agaspardcilia.chuchot.store;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/store")
@AllArgsConstructor
public class StoreController {
    private static final Logger LOGGER = LoggerFactory.getLogger(StoreController.class);
    private final StoreService service;

    @GetMapping("/inventory")
    public ResponseEntity<List<ItemDescription>> inventory() {
        try {
            return ResponseEntity.ok(service.inputInventory());
        } catch (IOException e) {
            LOGGER.error("Failed to fetch inventory", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/inventory/{jobId}")
    public ResponseEntity<List<ItemDescription>> inventory(@PathVariable UUID jobId) {
        try {
            List<ItemDescription> result = service.inputInventory(jobId);
            if (result == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return ResponseEntity.ok(result);
        } catch (IOException e) {
            LOGGER.error("Failed to fetch inventory", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/download/{name}")
    public ResponseEntity<Resource> download(@PathVariable String name) {
        try {
            Resource result = service.getInputResource(name);
            if (result == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(result.contentLength())
                    .header("Content-Disposition", "attachment; filename=\"%s\"".formatted(name))
                    .body(result);
        } catch (IOException e) {
            LOGGER.error("Failed to fetch inventory", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/download/{jobId}/{name}")
    public ResponseEntity<Resource> download(@PathVariable UUID jobId, @PathVariable String name) {
        try {
            Resource result = service.getJobInputResource(jobId, name);
            if (result == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .contentLength(result.contentLength())
                    .header("Content-Disposition", "attachment; filename=\"%s\"".formatted(name))
                    .body(result);
        } catch (IOException e) {
            LOGGER.error("Failed to fetch inventory", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ItemDescription> upload(@RequestParam MultipartFile file) {
        try {
            return ResponseEntity.ok(service.addItem(file.getBytes(), file.getName()));
        } catch (IOException e) {
            LOGGER.error("Failed to fetch inventory", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/accept")
    public UploadAccept getSupportedInputAccept() {
        return StoreService.SUPPORTED_INPUT;
    }
}
