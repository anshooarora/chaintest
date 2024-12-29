package com.aventstack.chainlp.embed;

public class AzureBlobSignedUrlResolver implements SignedUrlResolver {

    @Override
    public String resolve(final String path) {
        return path;
    }

}
