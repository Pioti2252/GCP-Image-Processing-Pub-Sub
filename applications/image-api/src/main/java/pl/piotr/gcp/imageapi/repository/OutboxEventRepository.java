package pl.piotr.gcp.imageapi.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import pl.piotr.gcp.imageapi.domain.OutboxEvent;
import pl.piotr.gcp.imageapi.domain.OutboxEventStatus;

public interface OutboxEventRepository
        extends JpaRepository<OutboxEvent, UUID> {

    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(
            OutboxEventStatus status,
            Pageable pageable
    );
}