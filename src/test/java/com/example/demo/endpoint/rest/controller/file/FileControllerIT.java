package com.example.demo.endpoint.rest.controller.file;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.conf.FacadeIT;
import com.example.demo.file.store.UploadedFile;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;

class FileControllerIT extends FacadeIT {

  @Autowired private TestRestTemplate restTemplate;

  @Test
  void postThenGet_withTextFile_shouldPersistItAndListIt() {
    var body = new LinkedMultiValueMap<String, Object>();
    body.add(
        "file",
        new ByteArrayResource("hello world".getBytes()) {
          @Override
          public String getFilename() {
            return "notes.txt";
          }
        });
    body.add("email", "francie@hei.mg");

    var headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    var request = new HttpEntity<>(body, headers);

    var postResponse = restTemplate.postForEntity("/files", request, UploadedFile.class);

    assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(postResponse.getBody()).isNotNull();
    assertThat(postResponse.getBody().fileName()).isEqualTo("notes.txt");
    assertThat(postResponse.getBody().email()).isEqualTo("francie@hei.mg");
    assertThat(postResponse.getBody().imageUrl()).isNull();
    assertThat(postResponse.getBody().createdAt()).isNotNull();

    var getResponse = restTemplate.getForEntity("/files", UploadedFile[].class);

    assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(getResponse.getBody()).isNotNull();
    assertThat(List.of(getResponse.getBody()))
        .anyMatch(uploadedFile -> uploadedFile.id().equals(postResponse.getBody().id()));
  }
}
