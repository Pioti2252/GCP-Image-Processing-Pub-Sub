package pl.piotr.gcp.imageapi.api;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> getApplicationInfo() {
        return ResponseEntity.ok(
                Map.of(
                        "application", "image-api",
                        "status", "running",
                        "timestamp", Instant.now().toString()
                )
        );
    }
}