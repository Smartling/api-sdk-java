/*
 * Copyright 2012 Smartling, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this work except in compliance with the License.
 * You may obtain a copy of the License in the LICENSE file, or at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.smartling.api.sdk.exceptions;

// TODO(AShesterov): refactor API-SDK: rename ApiException to SmartlingApiException

import com.google.gson.reflect.TypeToken;
import com.smartling.api.sdk.JsonReader;
import com.smartling.api.sdk.dto.ApiResponse;
import com.smartling.api.sdk.dto.ApiResponseWrapper;
import com.smartling.api.sdk.dto.EmptyResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Thrown when an exception has occurred when using the {@link com.smartling.api.sdk.FileApiClientAdapter}.
 */
public class ApiException extends Exception
{
    private static final long serialVersionUID = -397098626101615761L;

    private int httpCode;
    private List<String> messages = new ArrayList<>();

    ApiException(final String contents, List<String> messages, int httpCode)
    {
        super(contents);
        this.messages = messages;
        this.httpCode = httpCode;
    }

    ApiException(final Exception e)
    {
        super(e);
        messages.add(e.getMessage());
    }

    public static ApiException newException(String contents, int httpCode)
    {
        ApiResponse<EmptyResponse> apiResponse = JsonReader.parseApiResponse(contents, new TypeToken<ApiResponseWrapper<EmptyResponse>>()
                {
                }
        );
        String apiCode = apiResponse.getCode();
        List<String> messages = apiResponse.getMessages();
        return getApiException(contents, httpCode, apiCode, messages);
    }

    private static ApiException getApiException(final String contents, final int httpCode, final String apiCode, final List<String> messages)
    {
        switch (apiCode)
        {
            case "VALIDATION_ERROR":
                return new ValidationException(contents, messages, httpCode);
            case "AUTHENTICATION_ERROR":
                return new AuthenticationException(contents, messages, httpCode);
            case "AUTHORIZATION_ERROR":
                return new AuthorizationException(contents, messages, httpCode);
            case "RESOURCE_LOCKED":
                return new ResourceLockedException(contents, messages, httpCode);
            case "MAX_OPERATIONS_LIMIT_EXCEEDED":
                return new OperationsLimitExceeded(contents, messages, httpCode);
            case "GENERAL_ERROR":
                return new UnexpectedException(contents, messages, httpCode);
            case "MAINTENANCE_MODE_ERROR":
                return new ServiceTemporaryUnavailableException(contents, messages, httpCode);
            default:
                return new ApiException(contents, messages, httpCode);
        }
    }

    public static ApiException newException(IOException e)
    {
        return new ApiException(e);
    }

    public int getHttpCode()
    {
        return httpCode;
    }

    public List<String> getMessages()
    {
        return messages;
    }
}
