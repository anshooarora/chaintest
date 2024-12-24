package com.aventstack.chaintest.storage;

import com.aventstack.chaintest.domain.Embed;

import java.io.File;
import java.util.Map;

public class S3Client implements StorageClient {

    @Override
    public boolean create(Map<String, String> config) {
        return false;
    }

    @Override
    public void upload(String key, byte[] data) {

    }

    @Override
    public void upload(String key, String base64) {

    }

    @Override
    public void upload(String key, File file) {

    }

    @Override
    public void upload(Embed embed) {

    }

}
