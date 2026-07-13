package com.example.demo.endpoint.rest.controller.file;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import com.example.demo.file.image.ImageGrayscaleConverter;
import com.example.demo.file.store.S3Uploader;
import com.example.demo.file.store.UploadedFile;
import com.example.demo.file.store.UploadedFileRepository;
import com.example.demo.file.zip.FileTyper;
import com.example.demo.mail.Email;
import com.example.demo.mail.Mailer;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
public class FileController {

  UploadedFileRepository repository;
  FileTyper fileTyper;
  ImageGrayscaleConverter grayscaleConverter;
  S3Uploader s3Uploader;
  Mailer mailer;

  @PostMapping(value = "/files", consumes = MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UploadedFile> upload(
      @RequestParam("file") MultipartFile file, @RequestParam("email") String email)
      throws IOException, AddressException {

    var id = UUID.randomUUID();
    var tempFile = File.createTempFile("upload-" + id, "-" + file.getOriginalFilename());
    file.transferTo(tempFile);

    var mediaType = fileTyper.apply(tempFile);
    String imageUrl = null;

    if (MediaType.IMAGE_JPEG.equals(mediaType)) {
      var grayscaleBytes = grayscaleConverter.apply(tempFile);
      imageUrl = s3Uploader.upload(grayscaleBytes, id + ".jpg", MediaType.IMAGE_JPEG_VALUE);

      mailer.accept(
          new Email(
              new InternetAddress(email),
              List.of(),
              List.of(),
              "[poja] Votre image en noir et blanc",
              "<p>Voici le lien vers votre image convertie : <a href=\""
                  + imageUrl
                  + "\">"
                  + imageUrl
                  + "</a></p>",
              List.of()));
    }

    var uploadedFile =
        new UploadedFile(id, file.getOriginalFilename(), email, imageUrl, Instant.now());
    repository.save(uploadedFile);

    return new ResponseEntity<>(uploadedFile, HttpStatus.CREATED);
  }

  @GetMapping(value = "/files")
  public ResponseEntity<Collection<UploadedFile>> findAll() {
    return ResponseEntity.ok(repository.findAll());
  }
}
