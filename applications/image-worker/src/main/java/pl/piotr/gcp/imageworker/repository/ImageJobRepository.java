package pl.piotr.gcp.imageworker.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.piotr.gcp.imageworker.domain.ImageJob;

public interface ImageJobRepository
        extends JpaRepository<ImageJob, UUID> {
}