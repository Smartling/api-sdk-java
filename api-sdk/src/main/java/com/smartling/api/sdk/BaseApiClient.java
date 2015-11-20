package com.smartling.api.sdk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.smartling.api.sdk.exceptions.ApiException;
import com.smartling.api.sdk.file.response.ApiV2ResponseWrapper;
import com.smartling.api.sdk.file.response.Response;
import com.smartling.api.sdk.util.DateTypeAdapter;
import com.smartling.api.sdk.util.HttpUtils;
import com.smartling.web.api.v2.ResponseData;
import org.apache.commons.lang3.CharEncoding;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.Date;

public abstract class BaseApiClient
{
    private static final String APPLICATION_JSON_TYPE = "application/json";

    private final ObjectMapper objectMapper = new ObjectMapper();

    protected HttpUtils httpUtils;

    public HttpUtils getHttpUtils()
    {
        return httpUtils;
    }

    protected static <T extends ResponseData> Response<T> getApiV2Response(final String response, final TypeToken<ApiV2ResponseWrapper<T>> responseType)
    {
        String fixedResponse = response.replaceAll("\"data\"\\:\"\"", "\"data\":null");

        final GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new DateTypeAdapter());

        final Gson gson = builder.create();
        final ApiV2ResponseWrapper<T> responseWrapper = gson.fromJson(fixedResponse, responseType.getType());

        return responseWrapper.getResponse();
    }

    protected HttpPost createJsonPostRequest(final String url, final Object command) throws ApiException
    {
        final HttpPost httpPost = new HttpPost(url);
        final StringEntity stringEntity;
        try
        {
            stringEntity = new StringEntity(objectMapper.writeValueAsString(command));
            stringEntity.setContentType(APPLICATION_JSON_TYPE);
            stringEntity.setContentEncoding(CharEncoding.UTF_8);
            httpPost.setEntity(stringEntity);
        }
        catch (IOException e)
        {
            throw new ApiException(e);
        }

        return httpPost;
    }
}
