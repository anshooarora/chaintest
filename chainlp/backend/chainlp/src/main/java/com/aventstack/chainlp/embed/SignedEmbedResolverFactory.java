package com.aventstack.chainlp.embed;

import org.springframework.stereotype.Service;

@Service
public class SignedEmbedResolverFactory {

    private static final String CHAINTEST_USE_SIGNED_URLS = "CHAINTEST_USE_SIGNED_URLS";

    private final AwsS3SignedUrlResolver awsResolver = new AwsS3SignedUrlResolver();
    private final AzureBlobSignedUrlResolver azureResolver = new AzureBlobSignedUrlResolver();
    private final DefaultUrlResolver defaultResolver = new DefaultUrlResolver();

    public SignedEmbedResolverFactory() { }

    public SignedUrlResolver getResolver(final String path) {
        final boolean useSignedUrls = Boolean.parseBoolean(System.getenv(CHAINTEST_USE_SIGNED_URLS))
                || Boolean.parseBoolean(System.getProperty(CHAINTEST_USE_SIGNED_URLS, "false"));
        if (!useSignedUrls) {
            return defaultResolver;
        }

        if (path.toLowerCase().contains(".amazonaws.")) {
            return awsResolver;
        } else if (path.toLowerCase().contains(".windows.") || path.toLowerCase().contains(".azure.")) {
            return azureResolver;
        }
        return defaultResolver;
    }

}
