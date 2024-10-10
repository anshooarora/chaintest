package com.aventstack.chaintest.http;

import com.aventstack.chaintest.conf.Configuration;
import com.aventstack.chaintest.domain.ChainTestEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ChainTestApiClient {

    private static final Duration DEFAULT_REQUEST_TIMEOUT = Duration.ofSeconds(30);
    private static final HttpMethod DEFAULT_HTTP_METHOD = HttpMethod.POST;
    private static final String PROPERTY_SERVER_URL = "chaintest.server.url";
    private static final String PROPERTY_CLIENT_REQUEST_TIMEOUT = "chaintest.client.request-timeout-s";
    private static final String VERSION = "/api/v1/";

    private final HttpClient _httpClient;
    private final ObjectMapper _mapper;
    private final Duration _requestTimeout;
    private final URI _baseURI;

    private Map<String, String> _config;

    public ChainTestApiClient() throws IOException {
        final Builder builder = builder().defaultBuilder();
        _httpClient = builder.httpClient;
        _mapper = builder.objectMapper;
        _requestTimeout = builder.timeout;

        final Configuration config = new Configuration();
        config.load();
        _config = config.getConfig();
        final String serverURL = _config.get(PROPERTY_SERVER_URL);
        if (null == serverURL || serverURL.isBlank()) {
            throw new IllegalStateException("ChainTest endpoint was not provided by required property " + PROPERTY_SERVER_URL +
                    ". No such property was found in classpath resources or system environment");
        }
        _baseURI = URI.create(serverURL).resolve(VERSION);
    }

    public ChainTestApiClient(final Builder builder) {
        _httpClient = null == builder.httpClient
                ? HttpClient.newHttpClient() : builder.httpClient;
        _mapper = null == builder.objectMapper
                ? new ObjectMapper() : builder.objectMapper;
        _requestTimeout = null == builder.timeout
                ? DEFAULT_REQUEST_TIMEOUT : builder.timeout;
        _baseURI = builder.uri;

        if (null == builder.uri) {
            throw new IllegalArgumentException("Missing argument: uri");
        }
    }

    public ChainTestApiClient(final URI uri) {
        this(builder().defaultBuilder().withURI(uri));
    }

    public ChainTestApiClient(final String url) {
        this(builder().defaultBuilder().withURI(URI.create(url)));
    }

    public Map<String, String> getConfig() {
        return _config;
    }

    public static Builder builder() {
        return new Builder();
    }

    public <T extends ChainTestEntity> HttpResponse<String> send(final T entity, final HttpMethod method)
            throws IOException, InterruptedException {
        final HttpRequest request = createRequest(entity, method);
        return _httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public <T extends ChainTestEntity> HttpResponse<String> send(final T entity) throws IOException, InterruptedException {
        return send(entity, DEFAULT_HTTP_METHOD);
    }

    public <T extends ChainTestEntity> HttpResponse<T> send(final T entity, final Class<T> clazz, final HttpMethod method)
            throws IOException, InterruptedException {
        final HttpRequest request = createRequest(entity, method);
        return _httpClient.send(request, new JsonMappedBodyHandler<>(clazz));
    }

    public <T extends ChainTestEntity> HttpResponse<T> send(final T entity, final Class<T> clazz)
            throws IOException, InterruptedException {
        return send(entity, clazz, DEFAULT_HTTP_METHOD);
    }

    public <T extends ChainTestEntity> CompletableFuture<HttpResponse<String>> sendAsync(final T entity, final HttpMethod method)
            throws IOException {
        final HttpRequest request = createRequest(entity, method);
        return _httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public <T extends ChainTestEntity> CompletableFuture<HttpResponse<String>> sendAsync(final T entity) throws IOException {
        return sendAsync(entity, DEFAULT_HTTP_METHOD);
    }

    public <T extends ChainTestEntity> CompletableFuture<HttpResponse<T>> sendAsync(final T entity, final Class<T> clazz, final HttpMethod method)
            throws IOException {
        final HttpRequest request = createRequest(entity, method);
        return _httpClient.sendAsync(request, new JsonMappedBodyHandler<>(clazz));
    }

    public <T extends ChainTestEntity> CompletableFuture<HttpResponse<T>> sendAsync(final T entity, final Class<T> clazz)
            throws IOException {
        return sendAsync(entity, clazz, DEFAULT_HTTP_METHOD);
    }

    private <T extends ChainTestEntity> HttpRequest createRequest(final T entity, final HttpMethod method) throws IOException {
        final URI uri = getURI(entity);
        final String requestBody = _mapper.writeValueAsString(entity);
        return HttpRequest.newBuilder()
                .uri(uri)
                .timeout(_requestTimeout)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .method(method.getMethod(), HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
    }

    private <T extends ChainTestEntity> URI getURI(final T entity) {
        final String clz = entity.getClass().getSimpleName().toLowerCase();
        return _baseURI.resolve(clz + "s");
    }

    public static final class Builder {

        private HttpClient httpClient;
        private ObjectMapper objectMapper;
        private Duration timeout;
        private URI uri;

        public Builder withHttpClient(final HttpClient client) {
            httpClient = client;
            return this;
        }

        public Builder withObjectMapper(final ObjectMapper mapper) {
            objectMapper = mapper;
            return this;
        }

        public Builder withRequestTimeout(final Duration duration) {
            timeout = duration;
            return this;
        }

        public Builder withURI(final URI uri) {
            this.uri = uri.resolve(VERSION);
            return this;
        }

        public Builder withURI(final String url) {
            withURI(URI.create(url));
            return this;
        }

        public Builder defaultBuilder() {
            httpClient = HttpClient.newHttpClient();
            objectMapper = new ObjectMapper();
            timeout = DEFAULT_REQUEST_TIMEOUT;
            return this;
        }

        public ChainTestApiClient build() {
            return new ChainTestApiClient(this);
        }

    }

}
