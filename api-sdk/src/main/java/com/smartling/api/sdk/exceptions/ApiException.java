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

import com.smartling.api.sdk.dto.ApiCode;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Thrown when an exception has occurred when using the {@link com.smartling.api.sdk.FileApiClientAdapter}.
 */
public class ApiException extends Exception
{
    private static final long serialVersionUID = -397098626101615761L;

    private ApiCode apiCode;
    private int     httpCode;
    private List<String> messages = new ArrayList<>();

    ApiException(List<String> messages, final ApiCode apiCode, int httpCode)
    {
        super(StringUtils.join(messages, " ,"));
        this.messages = messages;
        this.apiCode = apiCode;
        this.httpCode = httpCode;
    }

    ApiException(final Exception e, ApiCode apiCode)
    {
        super(e);
        messages.add(e.getMessage());
        this.apiCode = apiCode;
    }

    public ApiCode getApiCode()
    {
        return apiCode;
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
