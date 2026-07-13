package com.example.demo.endpoint.rest.controller.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.file.image.ImageGrayscaleConverter;
import com.example.demo.file.store.S3Uploader;
import com.example.demo.file.store.UploadedFile;
import com.example.demo.file.store.UploadedFileRepository;
import com.example.demo.file.zip.FileTyper;
import com.example.demo.mail.Email;
import com.example.demo.mail.Mailer;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

class FileControllerTest {

  private UploadedFileRepository repository;
  private FileTyper fileTyper;
  private ImageGrayscaleConverter grayscaleConverter;
  private S3Uploader s3Uploader;
  private Mailer mailer;
  private FileController fileController;

  @BeforeEach
  void setUp() {
    repository = mock(UploadedFileRepository.class);
    fileTyper = mock(FileTyper.class);
    grayscaleConverter = mock(ImageGrayscaleConverter.class);
    s3Uploader = mock(S3Uploader.class);
    mailer = mock(Mailer.class);
    fileController =
        new FileController(repository, fileTyper, grayscaleConverter, s3Uploader, mailer);
  }

  @Test
  void upload_withJpegFile_shouldConvertUploadToS3AndSendEmail() throws IOException {
    var multipartFile =
        new MockMultipartFile(
            "file", "photo.jpg", MediaType.IMAGE_JPEG_VALUE, "fake-image-bytes".getBytes());
    var expectedS3Url = "https://dummy-bucket.s3.amazonaws.com/photo-bw.jpg";
    when(fileTyper.apply(any(File.class))).thenReturn(MediaType.IMAGE_JPEG);
    when(grayscaleConverter.apply(any(File.class))).thenReturn("grayscale-bytes".getBytes());
    when(s3Uploader.upload(any(byte[].class), anyString(), anyString())).thenReturn(expectedS3Url);

    var response = fileController.upload(multipartFile, "francie@hei.mg");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().fileName()).isEqualTo("photo.jpg");
    assertThat(response.getBody().email()).isEqualTo("francie@hei.mg");
    assertThat(response.getBody().imageUrl()).isEqualTo(expectedS3Url);
    assertThat(response.getBody().createdAt()).isNotNull();

    var emailCaptor = ArgumentCaptor.forClass(Email.class);
    verify(mailer).accept(emailCaptor.capture());
    assertThat(emailCaptor.getValue().to().getAddress()).isEqualTo("francie@hei.mg");
    assertThat(emailCaptor.getValue().htmlBody()).contains(expectedS3Url);

    verify(repository).save(response.getBody());
  }

  @Test
  void upload_withNonJpegFile_shouldPersistWithoutS3UploadOrEmail() throws IOException {
    var multipartFile =
        new MockMultipartFile(
            "file", "document.txt", MediaType.TEXT_PLAIN_VALUE, "hello".getBytes());
    when(fileTyper.apply(any(File.class))).thenReturn(MediaType.TEXT_PLAIN);

    var response = fileController.upload(multipartFile, "francie@hei.mg");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().imageUrl()).isNull();

    verify(grayscaleConverter, never()).apply(any());
    verify(s3Uploader, never()).upload(any(), anyString(), anyString());
    verify(mailer, never()).accept(any());
    verify(repository).save(response.getBody());
  }

  @Test
  void upload_withJpegFile_shouldReturnEntityMatchingUploadedFileFields() throws IOException {
    var multipartFile =
        new MockMultipartFile(
            "file", "avatar.jpeg", MediaType.IMAGE_JPEG_VALUE, "bytes".getBytes());
    when(fileTyper.apply(any(File.class))).thenReturn(MediaType.IMAGE_JPEG);
    when(grayscaleConverter.apply(any(File.class))).thenReturn("bw-bytes".getBytes());
    when(s3Uploader.upload(any(byte[].class), anyString(), anyString()))
        .thenReturn("https://dummy-bucket.s3.amazonaws.com/avatar-bw.jpeg");

    var response = fileController.upload(multipartFile, "test@hei.mg");
    var body = response.getBody();

    assertThat(body).isInstanceOf(UploadedFile.class);
    assertThat(body.id()).isNotNull();
  }

  @Test
  void findAll_withExistingFiles_shouldReturnThemAll() {
    var firstFile = new UploadedFile(UUID.randomUUID(), "a.jpg", "a@hei.mg", null, Instant.now());
    var secondFile = new UploadedFile(UUID.randomUUID(), "b.jpg", "b@hei.mg", null, Instant.now());
    when(repository.findAll()).thenReturn(List.of(firstFile, secondFile));

    var response = fileController.findAll();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).containsExactlyInAnyOrder(firstFile, secondFile);
  }

  @Test
  void findAll_withNoFile_shouldReturnEmptyCollection() {
    when(repository.findAll()).thenReturn(List.of());

    var response = fileController.findAll();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEmpty();
  }
}
