package com.aventstack.chaintest.storage;

public class StorageServiceFactory {

    public static StorageService getStorageService(final String storageService) {
        switch (storageService) {
            case "aws-s3":
            case "s3":
                return new AWSS3Client();
            case "azure-blob":
                return new AzureBlobClient();
            default:
                throw new IllegalArgumentException("Unknown storage service: " + storageService);
        }
    }

}
