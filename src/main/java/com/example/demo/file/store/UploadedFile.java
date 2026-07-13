package com.example.demo.file.store;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a file uploaded by a user.
 *
 * @param id unique identifier of the upload
 * @param fileName original name of the uploaded file
 * @param email email address of the person who uploaded the file
 * @param imageUrl S3 link to the black &amp; white version of the image (only set when the uploaded
 *     file is a JPEG image), {@code null} otherwise
 * @param createdAt date and time at which the upload was persisted
 */
public record UploadedFile(
    UUID id, String fileName, String email, String imageUrl, Instant createdAt) {}
