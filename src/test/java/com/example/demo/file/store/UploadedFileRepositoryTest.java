package com.example.demo.file.store;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UploadedFileRepositoryTest {

  private UploadedFileRepository repository;

  @BeforeEach
  void setUp() {
    repository = new UploadedFileRepository();
  }

  @Test
  void save_withNewFile_shouldPersistIt() {
    var id = UUID.randomUUID();
    var uploadedFile =
        new UploadedFile(id, "photo.jpg", "francie@hei.mg", null, Instant.now());

    var saved = repository.save(uploadedFile);

    assertThat(saved).isEqualTo(uploadedFile);
  }

  @Test
  void findById_withExistingId_shouldReturnFile() {
    var id = UUID.randomUUID();
    var uploadedFile =
        new UploadedFile(id, "photo.jpg", "francie@hei.mg", null, Instant.now());
    repository.save(uploadedFile);

    var found = repository.findById(id);

    assertThat(found).isPresent().contains(uploadedFile);
  }

  @Test
  void findById_withUnknownId_shouldReturnEmpty() {
    var found = repository.findById(UUID.randomUUID());

    assertThat(found).isEmpty();
  }

  @Test
  void findAll_withMultipleFiles_shouldReturnAllOfThem() {
    var firstFile =
        new UploadedFile(UUID.randomUUID(), "a.jpg", "a@hei.mg", null, Instant.now());
    var secondFile =
        new UploadedFile(UUID.randomUUID(), "b.jpg", "b@hei.mg", null, Instant.now());
    repository.save(firstFile);
    repository.save(secondFile);

    var all = repository.findAll();

    assertThat(all).containsExactlyInAnyOrder(firstFile, secondFile);
  }

  @Test
  void findAll_withNoFile_shouldReturnEmptyCollection() {
    var all = repository.findAll();

    assertThat(all).isEmpty();
  }
}
