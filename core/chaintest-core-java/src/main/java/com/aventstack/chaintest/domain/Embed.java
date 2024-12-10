package com.aventstack.chaintest.domain;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Data
public class Embed {

    private static final Logger log = LoggerFactory.getLogger(Embed.class);
    private static final List<String> SUPPORTED_MEDIA_TYPES = List.of(
            "image/png",
            "image/jpg",
            "image/jpeg",
            "image/gif",
            "image/bmp",
            "image/webp",
            "image/tiff"
    );

    private final UUID uuid = UUID.randomUUID();
    private String base64;
    private File file;
    private byte[] bytes;
    private String mediaType;

    public Embed() { }

    public Embed(final String base64, final String mediaType) {
        this.base64 = base64;
        this.mediaType = mediaType;
    }

    public Embed(final File file, final String mediaType) {
        this.file = file;
        this.mediaType = mediaType;
    }

    public Embed(final byte[] bytes, final String mediaType) {
        this.bytes = bytes;
        this.mediaType = mediaType;
    }

    public void save(final File filePath) throws IOException {
        if (!SUPPORTED_MEDIA_TYPES.contains(mediaType)) {
            log.debug("Unknown mediaType {}, skipping save", mediaType);
            return;
        }

        if (null != bytes) {
            Files.write(filePath.toPath(), bytes);
        } else if (null != base64 && !base64.isBlank()) {
            final byte[] data = Base64.getDecoder().decode(base64.getBytes());
            Files.write(filePath.toPath(), data);
        } else if (null != file) {
            Files.copy(file.toPath(), filePath.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } else {
            log.error("Unable to save Embed to {}. Source missing", filePath.getPath());
        }
    }

    public File makePath(final File parentDir) {
        return new File(parentDir, getName());
    }

    public String getName() {
        final String ext = this.mediaType.split("/")[1];
        return uuid + "." + ext;
    }

    public String id() {
        return uuid.toString();
    }

}
