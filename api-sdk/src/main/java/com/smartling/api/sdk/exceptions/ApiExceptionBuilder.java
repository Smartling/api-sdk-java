package com.smartling.api.sdk.exceptions;

import com.google.gson.reflect.TypeToken;
import com.smartling.api.sdk.JsonReader;
import com.smartling.api.sdk.dto.ApiCode;
import com.smartling.api.sdk.dto.ApiResponse;
import com.smartling.api.sdk.dto.ApiResponseWrapper;
import com.smartling.api.sdk.dto.EmptyResponse;

import java.io.IOException;
import java.util.List;

public class ApiExceptionBuilder
{
    public ApiException newException(String contents, int httpCode)
    {
        ApiResponse<EmptyResponse> apiResponse = JsonReader.parseApiResponse(contents, new TypeToken<ApiResponseWrapper<EmptyResponse>>()
                {
                }
        );
        ApiCode apiCode = apiResponse.getCode();
        List<String> messages = apiResponse.getMessages();
        return new ApiException(messages, apiCode, httpCode);
    }

    public ApiException newException(IOException e) {
        return new ApiException(e, ApiCode.NETWORK_ERROR);
    }
}
