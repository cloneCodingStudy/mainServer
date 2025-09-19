package com.itemrental.rentalService.community;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class CommunityImageService {

  private static final String DIRECTORY_PREFIX = "community/";

  private final S3Client s3Client;

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  public String uploadImage(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      return null;
    }

    String key = DIRECTORY_PREFIX + UUID.randomUUID() + resolveExtension(file.getOriginalFilename());
    PutObjectRequest request = PutObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .acl(ObjectCannedACL.PUBLIC_READ)
        .contentType(file.getContentType())
        .build();

    try (InputStream inputStream = file.getInputStream()) {
      s3Client.putObject(request, RequestBody.fromInputStream(inputStream, file.getSize()));
    } catch (IOException e) {
      throw new IllegalStateException("이미지 업로드 중 오류가 발생했습니다.", e);
    }

    return s3Client.utilities()
        .getUrl(GetUrlRequest.builder().bucket(bucket).key(key).build())
        .toExternalForm();
  }

  public void deleteImage(String imageUrl) {
    String key = extractKey(imageUrl);
    if (!StringUtils.hasText(key)) {
      return;
    }

    DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();
    s3Client.deleteObject(deleteRequest);
  }

  private String resolveExtension(String originalFileName) {
    if (!StringUtils.hasText(originalFileName) || !originalFileName.contains(".")) {
      return "";
    }
    return originalFileName.substring(originalFileName.lastIndexOf('.'));
  }

  private String extractKey(String imageUrl) {
    if (!StringUtils.hasText(imageUrl)) {
      return null;
    }
    try {
      URI uri = URI.create(imageUrl);
      String path = uri.getPath();
      if (path.startsWith("/")) {
        path = path.substring(1);
      }
      return path;
    } catch (IllegalArgumentException ex) {
      return null;
    }
  }
}
