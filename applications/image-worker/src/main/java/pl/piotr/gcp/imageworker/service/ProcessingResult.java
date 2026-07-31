package pl.piotr.gcp.imageworker.service;

public enum ProcessingResult {
    COMPLETED,
    RETRY,
    FAILED,
    ALREADY_COMPLETED
}