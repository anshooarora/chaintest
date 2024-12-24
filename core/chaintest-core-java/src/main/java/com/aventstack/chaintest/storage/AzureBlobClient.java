package com.aventstack.chaintest.storage;

import com.aventstack.chaintest.domain.Embed;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Base64;
import java.util.Map;

/**
 * Azure Blob Storage client provides the ability to upload files to Azure Blob Storage
 *
 * <p>Further reading:</p>
 * <ul>
 *  <li>
 *      <a href="https://learn.microsoft.com/en-us/java/api/com.azure.storage.blob.blobserviceclientbuilder">
 *      BlobServiceClientBuilder</a>
 *  </li>
 *  <li>
 *      <a href="https://learn.microsoft.com/en-us/java/api/com.azure.identity.defaultazurecredential?view=azure-java-stable">
 *      DefaultAzureCredential</a>
 *  </li>
 *  <li>
 *      <a href="https://github.com/Azure/azure-sdk-for-go/wiki/Set-up-Your-Environment-for-Authentication#configure-defaultazurecredential">
 *      Setting up your environment for authentication</a>
 *  </li>
 * </ul>
 */
public class AzureBlobClient implements StorageClient {

    private static final Logger log = LoggerFactory.getLogger(AzureBlobClient.class);
    private static final String AZURE_STORAGE_CONNECTION_STRING = "AZURE_STORAGE_CONNECTION_STRING";
    private static final String AZURE_TENANT_ID = "AZURE_TENANT_ID";
    private static final String AZURE_CLIENT_ID = "AZURE_CLIENT_ID";
    private static final String AZURE_CONTAINER_NAME = "AZURE_CONTAINER_NAME";

    private BlobContainerClient _containerClient;

    @Override
    public boolean create(final Map<String, String> config) {
        _containerClient = getContainerClient(config);
        return null != _containerClient;
    }

    private BlobServiceClient getServiceClient(final Map<String, String> config) {
        final BlobServiceClientBuilder builder = new BlobServiceClientBuilder();
        String connectionString = System.getenv(AZURE_STORAGE_CONNECTION_STRING);
        if (connectionString == null) {
            connectionString = System.getProperty(AZURE_STORAGE_CONNECTION_STRING);
        }
        if (connectionString == null && config.containsKey(STORAGE_SERVICE_ENDPOINT)) {
            connectionString = config.get(AZURE_STORAGE_CONNECTION_STRING);
        }
        if (connectionString != null) {
            return builder.connectionString(connectionString).buildClient();
        }
        if (System.getenv().containsKey(AZURE_TENANT_ID) || System.getenv().containsKey(AZURE_CLIENT_ID)) {
            log.debug(AZURE_TENANT_ID + " or " + AZURE_CLIENT_ID + " are set. " +
                    "Will use the DefaultAzureCredentialBuilder to authenticate.");
            return builder.credential(new DefaultAzureCredentialBuilder().build()).buildClient();
        }
        return null;
    }

    private BlobContainerClient getContainerClient(final Map<String, String> config) {
        final BlobServiceClient serviceClient = getServiceClient(config);
        if (serviceClient == null) {
            return null;
        }
        String containerName = System.getenv(AZURE_CONTAINER_NAME);
        if (containerName == null) {
            containerName = System.getProperty(AZURE_CONTAINER_NAME);
        }
        if (containerName == null && config.containsKey(STORAGE_CONTAINER_NAME)) {
            containerName = config.get(STORAGE_CONTAINER_NAME);
        }
        return containerName != null
                ? serviceClient.getBlobContainerClient(containerName)
                : serviceClient.getBlobContainerClient(DEFAULT_CONTAINER_NAME);
    }

    @Override
    public void upload(final String key, final byte[] data) {
        try (final ByteArrayInputStream inputStream = new ByteArrayInputStream(data)) {
            _containerClient.getBlobClient(key).upload(inputStream, data.length);
        } catch (Exception e) {
            log.error("Failed to upload key {} to Azure Blob Storage", key, e);
        }
    }

    @Override
    public void upload(final String key, final String base64) {
        final byte[] data = Base64.getDecoder().decode(base64.getBytes());
        upload(key, data);
    }

    @Override
    public void upload(final String key, final File file) {
        try {
            _containerClient.getBlobClient(key)
                    .uploadFromFile(file.getAbsolutePath());
        } catch (final Exception e) {
            log.error("Failed to upload key {} to Azure Blob Storage", key, e);
        }
    }

    @Override
    public void upload(final Embed embed) {
        if (null != embed.getBytes()) {
            upload(embed.id(), embed.getBytes());
        } else if (null != embed.getBase64() && !embed.getBase64().isBlank()) {
            upload(embed.id(), embed.getBase64());
        } else if (null != embed.getFile()) {
            upload(embed.id(), embed.getFile());
        } else {
            log.error("Unable to upload Embed to Azure Blob Storage. Source missing");
        }
    }

}
