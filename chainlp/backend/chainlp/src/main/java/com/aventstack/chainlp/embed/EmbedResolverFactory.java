package com.aventstack.chainlp.embed;

import com.aventstack.chainlp.embed.aws.AWSResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmbedResolverFactory {

    @Autowired
    private AWSResolver awsResolver;

    public StoragePathResolver getResolver(final String path) {
        if (path.toLowerCase().contains(".amazonaws.")) {
            return awsResolver;
        } else {
            return null;
        }
    }

}
