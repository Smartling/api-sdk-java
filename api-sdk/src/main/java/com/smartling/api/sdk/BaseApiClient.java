package com.smartling.api.sdk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.smartling.api.sdk.dto.file.StringResponse;
import com.smartling.api.sdk.exceptions.SmartlingApiException;
import com.smartling.api.sdk.file.response.ApiV2ResponseWrapper;
import com.smartling.api.sdk.file.response.Response;
import com.smartling.api.sdk.util.DateTypeAdapter;
import com.smartling.api.sdk.util.HttpUtils;
import com.smartling.web.api.v2.ResponseData;
import org.apache.commons.lang3.CharEncoding;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import java.util.Date;
import java.util.Objects;

public abstract class BaseApiClient
{
    public static final String DEFAULT_BASE_URL = "https://api.smartling.com";

    private final HttpUtils httpUtils = new HttpUtils();
    private final ProxyConfiguration proxyConfiguration;
    private final HttpClientConfiguration httpClientConfiguration;

    protected final String baseUrl;

    protected BaseApiClient()
    {
        this(DEFAULT_BASE_URL, null, null);
    }

    protected BaseApiClient(final String baseUrl, final ProxyConfiguration proxyConfiguration, final HttpClientConfiguration httpClientConfiguration)
    {
        this.baseUrl = Objects.requireNonNull(baseUrl, "Base URL can not be null");
        this.proxyConfiguration = proxyConfiguration;
        this.httpClientConfiguration = httpClientConfiguration;
    }

    protected StringResponse executeRequest(final HttpRequestBase request) throws SmartlingApiException
    {
        return httpUtils.executeHttpCall(request, proxyConfiguration, httpClientConfiguration);
    }

    protected static <T extends ResponseData> Response<T> getApiV2Response(final String response, final TypeToken<ApiV2ResponseWrapper<T>> responseType) throws SmartlingApiException
    {
        //Replace of empty data response to make Gson work properly
        String fixedResponse = response.replaceAll("\"data\":\"\"", "\"data\":null");

        try
        {
            final GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new DateTypeAdapter());

            final Gson gson = builder.create();
            final ApiV2ResponseWrapper<T> responseWrapper = gson.fromJson(fixedResponse, responseType.getType());

            if (!isValidResponse(responseWrapper))
            {
                throw new SmartlingApiException(String.format("Response hasn't been parsed correctly [response='%s']", response));
            }

            return responseWrapper.getResponse();
        }
        catch (JsonParseException e)
        {
            throw new SmartlingApiException(String.format("Can't parse response as JSON [response='%s']", response), e);
        }
    }

    private static <T extends ResponseData> boolean isValidResponse(ApiV2ResponseWrapper<T> responseWrapper)
    {
        return responseWrapper != null && responseWrapper.getResponse().getCode() != null;
    }

    protected static HttpPost createJsonPostRequest(final String url, final Object command) throws SmartlingApiException
    {
        final String json = new Gson().toJson(command);

        final HttpPost httpPost = new HttpPost(url);
        final StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
        stringEntity.setContentEncoding(CharEncoding.UTF_8);
        httpPost.setEntity(stringEntity);

        return httpPost;
    }
}
