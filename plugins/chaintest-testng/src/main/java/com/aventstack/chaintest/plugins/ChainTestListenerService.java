package com.aventstack.chaintest.plugins;

import com.aventstack.chaintest.domain.Embed;
import com.aventstack.chaintest.service.ChainPluginService;

import java.io.File;

import static com.aventstack.chaintest.plugins.ChainTestListener.getExternalId;

public class ChainTestListenerService {

    private ChainTestListenerService() { }

    public static void log(final String message) {
        ChainPluginService.getInstance().log(getExternalId(Thread.currentThread().getId()), message);
    }

    public static void embed(final byte[] data, final String mimeType) {
        ChainPluginService.getInstance().embed(getExternalId(Thread.currentThread().getId()), new Embed(data, mimeType));
    }

    public static void embed(final File file, final String mimeType) {
        ChainPluginService.getInstance().embed(getExternalId(Thread.currentThread().getId()), new Embed(file, mimeType));
    }

    public static void embed(final String base64, final String mimeType) {
        ChainPluginService.getInstance().embed(getExternalId(Thread.currentThread().getId()), new Embed(base64, mimeType));
    }

}
