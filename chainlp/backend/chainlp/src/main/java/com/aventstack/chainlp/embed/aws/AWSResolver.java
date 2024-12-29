package com.aventstack.chainlp.embed.aws;

import com.aventstack.chainlp.embed.PresignedUrlResolver;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Uri;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URI;
import java.time.Duration;

@Slf4j
public class AWSResolver implements PresignedUrlResolver {

    public AWSResolver() { }

    @Override
    public String resolve(final String path) {
        log.debug("Resolving path: [{}]", path);
        final URI uri = URI.create(path);
        final S3Client s3Client = S3Client.create();
        final S3Uri s3URI = s3Client.utilities().parseUri(uri);
        final String bucket = s3URI.bucket()
                .orElseThrow(() -> new IllegalArgumentException("Bucket not found"));
        final String key = s3URI.key()
                .orElseThrow(() -> new IllegalArgumentException("Key not found"));
        return createPresignedGetUrl(bucket, key);
    }

    public String createPresignedGetUrl(final String bucket, final String key) {
        log.debug("Creating presigned URL for bucket: [{}] and key: [{}]", bucket, key);
        try (final S3Presigner presigner = S3Presigner.create()) {
            final GetObjectRequest objectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            final GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .getObjectRequest(objectRequest)
                    .build();
            final PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
            log.info("Presigned URL: [{}]", presignedRequest.url().toString());
            log.info("HTTP method: [{}]", presignedRequest.httpRequest().method());
            return presignedRequest.url().toExternalForm();
        }
    }

}
