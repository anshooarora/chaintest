package com.aventstack.chaintest.storage;

import com.aventstack.chaintest.domain.Embed;
import com.aventstack.chaintest.domain.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.File;
import java.util.Base64;
import java.util.Map;

/**
 * StorageService provides the ability to upload files to a storage service
 *
 * <p>Further reading:</p>
 * <ul>
 *   <li>
 *       <a href="https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/credentials-chain.html">
 *           Default credentials provider chain</a>
 *   </li>
 *   <li>
 *       <a href="https://docs.aws.amazon.com/cli/v1/userguide/cli-configure-envvars.html">
 *       Environment variables</a>
 *   </li>
 * </ul>
 */
public class AWSS3Client implements StorageService {

    private static final Logger log = LoggerFactory.getLogger(AWSS3Client.class);
    private static final String AWS_ACCESS_KEY_ID = "AWS_ACCESS_KEY_ID";
    private static final String AWS_SECRET_ACCESS_KEY = "AWS_SECRET_ACCESS_KEY";
    private static final String AWS_DEFAULT_REGION = "AWS_DEFAULT_REGION";

    private S3Client _client;
    private String _bucket;

    @Override
    public boolean create(final Map<String, String> config) {
        /*AwsCredentials credentials;
        if (config.containsKey(AWS_ACCESS_KEY_ID) && config.containsKey(AWS_SECRET_ACCESS_KEY) && config.containsKey(AWS_DEFAULT_REGION)) {
            credentials = AwsBasicCredentials.create(config.get(AWS_ACCESS_KEY_ID), config.get(AWS_SECRET_ACCESS_KEY));
        }
        if (System.getenv(AWS_ACCESS_KEY_ID) != null && System.getenv(AWS_SECRET_ACCESS_KEY) != null && System.getenv(AWS_DEFAULT_REGION) != null) {
            credentials = AwsBasicCredentials.create(System.getenv(AWS_ACCESS_KEY_ID), System.getenv(AWS_SECRET_ACCESS_KEY));
        }
        if (System.getProperty(AWS_ACCESS_KEY_ID) != null && System.getProperty(AWS_SECRET_ACCESS_KEY) != null && System.getProperty(AWS_DEFAULT_REGION) != null) {
            credentials = AwsBasicCredentials.create(System.getProperty(AWS_ACCESS_KEY_ID), System.getProperty(AWS_SECRET_ACCESS_KEY));
        }
        */
        _client = S3Client.builder()
                .httpClientBuilder(ApacheHttpClient.builder())
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .build();
        _bucket = config.getOrDefault(STORAGE_CONTAINER_NAME, DEFAULT_CONTAINER_NAME);
        return false;
    }

    private void createBucket(final String bucketName) {
        try {
            _client.createBucket(CreateBucketRequest
                    .builder()
                    .bucket(bucketName)
                    .build());
            log.debug("Creating bucket: {}", bucketName);
            _client.waiter().waitUntilBucketExists(HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build());
            log.debug("{} is ready.", bucketName);
        } catch (final S3Exception e) {
            log.error("Failed to create bucket", e);
        }
    }

    @Override
    public void upload(final Test test, final String key, final byte[] data) {
        _client.putObject(PutObjectRequest.builder().bucket(_bucket).key(key)
                        .build(),
                RequestBody.fromBytes(data));
        assignURL(test, key);
    }

    private void assignURL(final Test test, final String key) {
        test.addScreenshotURL(
                _client.utilities().getUrl(builder -> builder.bucket(_bucket).key(key)).toExternalForm());
    }

    @Override
    public void upload(final Test test, String key, String base64) {
        final byte[] data = Base64.getDecoder().decode(base64.getBytes());
        upload(test, key, data);
    }

    @Override
    public void upload(final Test test, final String key, final File file) {
        _client.putObject(PutObjectRequest.builder().bucket(_bucket).key(key)
                        .build(),
                RequestBody.fromFile(file));
        assignURL(test, key);
    }

    @Override
    public void upload(final Test test, Embed embed) {
        if (null != embed.getBytes()) {
            upload(test, embed.id(), embed.getBytes());
        } else if (null != embed.getBase64() && !embed.getBase64().isBlank()) {
            upload(test, embed.id(), embed.getBase64());
        } else if (null != embed.getFile()) {
            upload(test, embed.id(), embed.getFile());
        } else {
            log.error("Unable to upload Embed to Azure Blob Storage. Source missing");
        }
    }

    @Override
    public void close() {
        _client.close();
    }

}
