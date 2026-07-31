package pl.piotr.gcp.imageapi.api;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import pl.piotr.gcp.imageapi.domain.ImageJob;
import pl.piotr.gcp.imageapi.dto.ImageJobResponse;
import pl.piotr.gcp.imageapi.service.ImageJobService;

@RestController
@RequestMapping("/api/v1/image-jobs")
public class ImageJobController {

    private final ImageJobService imageJobService;

    public ImageJobController(ImageJobService imageJobService) {
        this.imageJobService = imageJobService;
    }

    @PostMapping
    public ResponseEntity<ImageJobResponse> createJob(
            @RequestPart("file") MultipartFile file
    ) {
        ImageJob imageJob = imageJobService.createJob(file);

        URI location = URI.create(
                "/api/v1/image-jobs/" + imageJob.getId()
        );

        return ResponseEntity
                .created(location)
                .body(ImageJobResponse.from(imageJob));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImageJobResponse> getJob(
            @PathVariable UUID id
    ) {
        return imageJobService.findById(id)
                .map(ImageJobResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}