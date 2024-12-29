package com.aventstack.chainlp.embed;

public class DefaultUrlResolver implements SignedUrlResolver {

    @Override
    public String resolve(final String path) {
        return path;
    }
}
