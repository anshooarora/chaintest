package com.aventstack.chaintest.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class JsonMappedBodyHandler<T> implements HttpResponse.BodyHandler<T> {

    private static final Logger log = LoggerFactory.getLogger(JsonMappedBodyHandler.class);

    private final Class<T> _clazz;
    private final ObjectMapper _objectMapper;

    public JsonMappedBodyHandler(final Class<T> clazz, final ObjectMapper objectMapper) {
        _clazz = clazz;
        _objectMapper = objectMapper;
    }

    @Override
    public HttpResponse.BodySubscriber<T> apply(final HttpResponse.ResponseInfo responseInfo) {
        final HttpResponse.BodySubscriber<String> bodySubscriber = HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8);
        return HttpResponse.BodySubscribers.mapping(bodySubscriber,
                (final String body) -> {
                    log.debug("Received response from service: {}", body);
                    try {
                        return _objectMapper.readValue(body, _clazz);
                    } catch (final Exception e) {
                        log.error("Failed to map expected response of type {}. Received response: {}",
                                _clazz.getSimpleName(), body, e);
                        throw new RuntimeException(e);
                    }
                });
    }

}
