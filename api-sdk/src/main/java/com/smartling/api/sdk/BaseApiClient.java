package com.smartling.api.sdk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.smartling.api.sdk.exceptions.SmartlingApiException;
import com.smartling.api.sdk.file.response.ApiV2ResponseWrapper;
import com.smartling.api.sdk.file.response.Response;
import com.smartling.api.sdk.util.DateTypeAdapter;
import com.smartling.api.sdk.util.HttpUtils;
import com.smartling.web.api.v2.ResponseData;
import org.apache.commons.lang3.CharEncoding;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.util.Date;

public abstract class BaseApiClient
{
    public static final String DEFAULT_API_GATEWAY_URL = "https://api.smartling.com";

    private static final String APPLICATION_JSON_TYPE = "application/json";

    protected HttpUtils httpUtils = new HttpUtils();

    public HttpUtils getHttpUtils()
    {
        return httpUtils;
    }

    public void setHttpUtils(final HttpUtils httpUtils)
    {
        this.httpUtils = httpUtils;
    }

    protected static <T extends ResponseData> Response<T> getApiV2Response(final String response, final TypeToken<ApiV2ResponseWrapper<T>> responseType) throws SmartlingApiException
    {
        //Replace of empty data response to make Gson work properly
        String fixedResponse = response.replaceAll("\"data\"\\:\"\"", "\"data\":null");

        final GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new DateTypeAdapter());

        final Gson gson = builder.create();
        final ApiV2ResponseWrapper<T> responseWrapper = gson.fromJson(fixedResponse, responseType.getType());

        if (isValidResponse(responseWrapper))
        {
            return responseWrapper.getResponse();
        }
        else
        {
            throw new SmartlingApiException(String.format("Response hasn't been parsed correctly [response='%s']", response));
        }
    }

    private static <T extends ResponseData> boolean isValidResponse(ApiV2ResponseWrapper<T> responseWrapper)
    {
        return responseWrapper != null && responseWrapper.getResponse().getCode() != null;
    }

    protected HttpPost createJsonPostRequest(final String url, final Object command) throws SmartlingApiException
    {
        final HttpPost httpPost = new HttpPost(url);
        final StringEntity stringEntity;
        try
        {
            Gson gson = new Gson();
            stringEntity = new StringEntity(gson.toJson(command));
            stringEntity.setContentType(APPLICATION_JSON_TYPE);
            stringEntity.setContentEncoding(CharEncoding.UTF_8);
            httpPost.setEntity(stringEntity);
        }
        catch (IOException e)
        {
            throw new SmartlingApiException(e);
        }

        return httpPost;
    }
}
