package com.example.demo.file.store;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/**
 * Simple in-memory storage for {@link UploadedFile}. No database is set up in this project yet,
 * so uploads only live for the lifetime of the running instance.
 */
@Component
public class UploadedFileRepository {

  private final Map<UUID, UploadedFile> store = new ConcurrentHashMap<>();

  public UploadedFile save(UploadedFile uploadedFile) {
    store.put(uploadedFile.id(), uploadedFile);
    return uploadedFile;
  }

  public Optional<UploadedFile> findById(UUID id) {
    return Optional.ofNullable(store.get(id));
  }

  public Collection<UploadedFile> findAll() {
    return store.values();
  }
}
