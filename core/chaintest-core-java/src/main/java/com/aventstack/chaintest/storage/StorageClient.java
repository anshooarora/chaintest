package com.aventstack.chaintest.storage;

import com.aventstack.chaintest.domain.Embed;

import java.io.File;
import java.util.Map;

public interface StorageClient {

    String DEFAULT_CONTAINER_NAME = "chaintest";
    String STORAGE_SERVICE = "chaintest.storage.service";
    String STORAGE_SERVICE_ENDPOINT = "chaintest.storage.service.endpoint";
    String STORAGE_CONTAINER_NAME = "chaintest.storage.service.container-name";

    boolean create(final Map<String, String> config);

    void upload(final String key, final byte[] data);
    void upload(final String key, final String base64);
    void upload(final String key, final File file);
    void upload(final Embed embed);

    void close();

}
