package com.example.todo.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.todo.common.GeneralException;
import com.example.todo.common.Status;
import com.example.todo.model.request.RefreshTokenRequest;
import com.example.todo.model.response.RefreshTokenResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.type.LogicalType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Random;
import java.util.function.BiFunction;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpClientHelper {
    private static HttpClientHelper instance;
    private static final String TAG = HttpClientHelper.class.getSimpleName();
    private OkHttpClient httpClient;
    private CustomInterceptor interceptor = new CustomInterceptor();
    private TokenManager tokenManager;
    private ObjectMapper objectMapper;

    public HttpClientHelper(Context context) {
        this.tokenManager = TokenManager.getInstance(context);
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.coercionConfigFor(LogicalType.Enum).setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsNull);
        this.setAccessToken(tokenManager.getAccessToken());
        this.setRefreshToken(tokenManager.getRefreshToken());
        this.httpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
    }

    public static synchronized HttpClientHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (HttpClientHelper.class) {
                if (null == instance) {
                    instance = new HttpClientHelper(context);
                }
            }
        }
        return instance;
    }

    private class CustomInterceptor implements Interceptor {
        private String refreshToken;
        private String accessToken;
        private boolean isRefreshingToken = false;
        private final Deque<Request> requestQueue = new ArrayDeque<>();

        @NonNull
        @Override
        public synchronized Response intercept(@NonNull Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Response response = chain.proceed(originalRequest);

            // Check if the response is due to an expired or invalid token
            if (response.code() == 401) {
                // Synchronize access to token refreshing
                if (!isRefreshingToken) {
                    synchronized (this) {
                        isRefreshingToken = true;
                        refreshToken();
                        Request retryRequest = originalRequest.newBuilder()
                                .header("Authorization", "jwt " + accessToken)
                                .build();
                        response = chain.proceed(retryRequest);
                    }
                    isRefreshingToken = false;
                    synchronized (requestQueue) {
                        while (!requestQueue.isEmpty()) {
                            Request queuedRequest = requestQueue.poll();
                            Request newRequest = queuedRequest.newBuilder()
                                    .header("Authorization", "jwt " + accessToken)
                                    .build();
                            chain.proceed(newRequest);

                        }
                    }
                } else {
                    requestQueue.add(originalRequest);
                }
            }

            return response;
        }

        private void refreshToken() throws IOException {
            RefreshTokenRequest body = new RefreshTokenRequest();
            body.setRefreshToken(this.refreshToken);
            RefreshTokenResponse response = makeQuery(
                    generateRid(),
                    new Request.Builder()
                            .post(getBodyJson(generateRid(), body))
                            .header("Content-Type", "application/json"),
                    buildUrl("/auth/refresh-token", null),
                    RefreshTokenResponse.class,
                    null
            );
            String newAccessToken = response.getAccessToken();
            setAccessToken(newAccessToken);
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public String getAccessToken() {
            return accessToken;
        }
    }

    public <T> T get(Uri uri, Class<T> clazz) {
        return makeQuery(generateRid(), new Request.Builder().get(), uri, clazz, null);
    }


    public <T> T get(Uri uri, TypeReference<T> clazz) {
        return makeQuery(generateRid(), new Request.Builder().get(), uri, null, clazz);
    }


    public <T> T delete(Uri uri, Class<T> clazz) {
        return makeQuery(generateRid(), new Request.Builder().delete(), uri, clazz, null);
    }

    public <T> T post(Uri uri, Object body, Class<T> clazz) {
        String rid = generateRid();
        return makeQuery(
                rid,
                new Request.Builder()
                        .post(this.getBodyJson(rid, body))
                        .header("Content-Type", "application/json"),
                uri,
                clazz,
                null
        );
    }

    public <T> T put(Uri uri, Object body, Class<T> clazz) {
        String rid = generateRid();
        return makeQuery(
                rid,
                new Request.Builder()
                        .put(this.getBodyJson(rid, body))
                        .header("Content-Type", "application/json"),
                uri,
                clazz,
                null
        );
    }

    public <T> T makeQuery(String rid, Request.Builder builder, Uri uri, Class<T> clazz, TypeReference<T> typeReference) {
        return this.makeQueryNoToken(rid, builder.header("Authorization", "jwt " + this.getAccessToken()), uri, clazz, typeReference);
    }

    public <T> T makeQueryNoToken(String rid, Request.Builder builder, Uri uri, Class<T> clazz, TypeReference<T> typeReference) {
        return makeQuery(rid, builder, uri, clazz, typeReference, this::handleQueryError);
    }

    public <T> T makeQuery(
            String rid,
            Request.Builder builder,
            Uri uri,
            Class<T> clazz,
            TypeReference<T> typeReference,
            BiFunction<Response, String, T> errorHandler
    ) {
        Request.Builder newBuilder;
        newBuilder = builder
                .url(uri.toString())
                .header("Accept", "application/json")
                .header("rid", rid)
                .header("Cache-Control", "no-cache");
        Request request = newBuilder.build();
        Log.i(TAG, String.format("%s request %s:%s", rid, request.method(), uri));
        long startTime = System.currentTimeMillis();
        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = (response.body() != null) ? response.body().string() : "";
            Log.i(TAG, String.format("%s request %s with status: %d and response: %s --- took: %d ms", rid, uri, response.code(),
                    responseBody, System.currentTimeMillis() - startTime));
            if (response.code() == 200 || response.code() == 201) {
                if (clazz == null) {
                    return objectMapper.readValue(responseBody, typeReference);
                }
                return objectMapper.readValue(responseBody, clazz);
            } else {
                if (errorHandler != null) {
                    return errorHandler.apply(response, responseBody);
                } else {
                    throw new GeneralException("QUERY_STATUS_CODE_" + response.code());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public RequestBody getBodyJson(String rid, Object body) {
        String bodyJson;
        try {
            bodyJson = this.objectMapper.writeValueAsString(body);
            Log.i(TAG, String.format("%s request body json %s", rid, bodyJson));
            return RequestBody.create(bodyJson.getBytes(StandardCharsets.UTF_8));
        } catch (JsonProcessingException e) {
            Log.e(TAG, String.format("%s cannot write object to json string %s", rid, body), e);
            throw new GeneralException();
        }
    }

    public <T> T handleQueryError(Response response, String responseBody) {
        if (response.code() == 400) {
            Status status = null;
            try {
                status = objectMapper.readValue(responseBody, Status.class);
            } catch (JsonProcessingException e) {
                throw new GeneralException().source(e);
            }
            throw status.create();
        } else {
            throw new GeneralException();
        }
    }

    public Uri buildUrl(String path, Map<String, Object> queryParams) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .encodedAuthority("192.168.1.118:8083")
                .path("/api/v1" + path);
        if (queryParams != null) {
            queryParams.forEach((key, value) -> {
                builder.appendQueryParameter(key, value.toString());
            });
        }
        return builder.build();
    }

    private String generateRid() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

    public void setRefreshToken(String refreshToken) {
        interceptor.setRefreshToken(refreshToken);
    }

    public void setAccessToken(String accessToken) {
        interceptor.setAccessToken(accessToken);
    }

    public String getRefreshToken() {
        return interceptor.getRefreshToken();
    }

    public String getAccessToken() {
        return interceptor.getAccessToken();
    }
}
