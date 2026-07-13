package com.example.demo.file.store;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@AllArgsConstructor
public class S3Uploader {

  private final S3Client s3Client;
  private final S3Conf s3Conf;

  public String upload(byte[] content, String key, String contentType) {
    s3Client.putObject(
        PutObjectRequest.builder()
            .bucket(s3Conf.getBucket())
            .key(key)
            .contentType(contentType)
            .build(),
        RequestBody.fromBytes(content));
    return "https://%s.s3.amazonaws.com/%s".formatted(s3Conf.getBucket(), key);
  }
}
