package com.aventstack.chainlp.embed.aws;

import com.aventstack.chainlp.embed.PresignedUrlResolver;

public class AzureResolver implements PresignedUrlResolver {

    @Override
    public String resolve(final String path) {
        return path;
    }

}
