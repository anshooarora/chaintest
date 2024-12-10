package com.aventstack.chaintest.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

public class IOUtil {

    private static final Logger log = LoggerFactory.getLogger(IOUtil.class);

    public static void copyClassPathResource(final Class<?> clazz, final String resourcePath, final String copyToPath) {
        try {
            final InputStream in = clazz.getResourceAsStream(resourcePath);
            if (null == in) {
                throw new IllegalArgumentException("Unable to find resource " + resourcePath);
            }

            final FileOutputStream out = new FileOutputStream(copyToPath);
            final byte[] b = new byte[1024];
            int noOfBytes = 0;
            while ((noOfBytes = in.read(b)) != -1) {
                out.write(b, 0, noOfBytes);
            }
            in.close();
            out.close();
        } catch (final FileNotFoundException e) {
            log.error("Resource file {} was not found", resourcePath, e);
        } catch (final Exception e) {
            log.error("An exception occurred while moving resource {} to {}", resourcePath, copyToPath, e);
        }
    }

}