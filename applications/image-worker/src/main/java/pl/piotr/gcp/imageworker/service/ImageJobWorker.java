package pl.piotr.gcp.imageworker.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.piotr.gcp.imageworker.domain.ImageJob;
import pl.piotr.gcp.imageworker.domain.ImageJobStatus;
import pl.piotr.gcp.imageworker.repository.ImageJobRepository;

@Service
public class ImageJobWorker {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ImageJobWorker.class);

    private final ImageJobRepository imageJobRepository;
    private final ImageProcessor imageProcessor;
    private final int maxAttempts;

    public ImageJobWorker(
            ImageJobRepository imageJobRepository,
            ImageProcessor imageProcessor,
            @Value("${app.worker.max-attempts:5}")
            int maxAttempts
    ) {
        this.imageJobRepository = imageJobRepository;
        this.imageProcessor = imageProcessor;
        this.maxAttempts = maxAttempts;
    }

    @Transactional
    public ProcessingResult processJob(
            UUID jobId,
            String correlationId
    ) {
        ImageJob imageJob = imageJobRepository.findById(jobId)
                .orElseThrow(
                        () -> new IllegalStateException(
                                "Nie znaleziono zadania: " + jobId
                        )
                );

        if (imageJob.getStatus() == ImageJobStatus.COMPLETED) {
            LOGGER.info(
                    "Job already completed: jobId={}, correlationId={}",
                    jobId,
                    correlationId
            );

            return ProcessingResult.ALREADY_COMPLETED;
        }

        if (
                imageJob.getStatus() != ImageJobStatus.PENDING
                        && imageJob.getStatus() != ImageJobStatus.FAILED
        ) {
            LOGGER.warn(
                    "Job is currently not processable: jobId={}, status={}",
                    jobId,
                    imageJob.getStatus()
            );

            return ProcessingResult.RETRY;
        }

        imageJob.markAsProcessing();
        imageJobRepository.saveAndFlush(imageJob);

        LOGGER.info(
                "Image processing started: jobId={}, attempt={}, correlationId={}",
                jobId,
                imageJob.getProcessingAttempts(),
                correlationId
        );

        try {
            imageProcessor.process(imageJob);

            imageJob.markAsCompleted();
            imageJobRepository.save(imageJob);

            LOGGER.info(
                    "Image processing completed: jobId={}, attempt={}, correlationId={}",
                    jobId,
                    imageJob.getProcessingAttempts(),
                    correlationId
            );

            return ProcessingResult.COMPLETED;

        } catch (Exception exception) {
            String error = exception.getMessage();

            if (imageJob.getProcessingAttempts() >= maxAttempts) {
                imageJob.markAsFailed(error);
                imageJobRepository.save(imageJob);

                LOGGER.error(
                        "Image processing permanently failed: jobId={}, attempt={}, correlationId={}",
                        jobId,
                        imageJob.getProcessingAttempts(),
                        correlationId,
                        exception
                );

                return ProcessingResult.FAILED;
            }

            imageJob.markForRetry(error);
            imageJobRepository.save(imageJob);

            LOGGER.warn(
                    "Image processing will be retried: jobId={}, attempt={}, correlationId={}",
                    jobId,
                    imageJob.getProcessingAttempts(),
                    correlationId,
                    exception
            );

            return ProcessingResult.RETRY;
        }
    }
}