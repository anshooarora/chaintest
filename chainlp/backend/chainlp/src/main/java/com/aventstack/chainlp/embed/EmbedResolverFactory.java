package com.aventstack.chainlp.embed;

import com.aventstack.chainlp.embed.aws.AWSResolver;
import org.springframework.stereotype.Service;

@Service
public class EmbedResolverFactory {

    private final AWSResolver awsResolver = new AWSResolver();

    public EmbedResolverFactory() { }

    public PresignedUrlResolver getResolver(final String path) {
        if (path.toLowerCase().contains(".amazonaws.")) {
            return awsResolver;
        } else {
            return null;
        }
    }

}
