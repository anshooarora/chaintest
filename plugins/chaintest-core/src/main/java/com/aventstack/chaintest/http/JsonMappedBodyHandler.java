package com.aventstack.chaintest.http;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class JsonMappedBodyHandler<T> implements HttpResponse.BodyHandler<T> {

    private final Class<T> _clazz;

    public JsonMappedBodyHandler(final Class<T> clazz) {
        _clazz = clazz;
    }

    @Override
    public HttpResponse.BodySubscriber<T> apply(final HttpResponse.ResponseInfo responseInfo) {
        final HttpResponse.BodySubscriber<String> bodySubscriber = HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8);
        return HttpResponse.BodySubscribers.mapping(bodySubscriber,
                (final String body) -> {
                    try {
                        final ObjectMapper objectMapper = new ObjectMapper();
                        return objectMapper.readValue(body, _clazz);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
    }

}
