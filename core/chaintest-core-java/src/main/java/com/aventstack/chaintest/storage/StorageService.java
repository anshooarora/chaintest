package com.aventstack.chaintest.storage;

import com.aventstack.chaintest.domain.Embed;
import com.aventstack.chaintest.domain.Test;

import java.io.File;
import java.util.Map;

public interface StorageService {

    String DEFAULT_CONTAINER_NAME = "chaintest";
    String STORAGE_SERVICE = "chaintest.storage.service";
    String STORAGE_SERVICE_ENDPOINT = "chaintest.storage.service.endpoint";
    String STORAGE_CONTAINER_NAME = "chaintest.storage.service.container-name";

    boolean create(final Map<String, String> config);

    void upload(final Test test, final String key, final byte[] data);
    void upload(final Test test, final String key, final String base64);
    void upload(final Test test, final String key, final File file);
    void upload(final Test test, final Embed embed);

    void close();

}
