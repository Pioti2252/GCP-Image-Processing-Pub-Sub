package pl.piotr.gcp.imageapi.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.piotr.gcp.imageapi.domain.ImageJob;

public interface ImageJobRepository extends JpaRepository<ImageJob, UUID> {
}