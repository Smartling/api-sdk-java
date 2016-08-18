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

import com.smartling.api.sdk.file.response.Error;
import com.smartling.api.sdk.util.HttpUtils;
import org.apache.http.Header;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmartlingApiException extends Exception
{
    private static final long serialVersionUID = -397098626101615761L;

    private final List<Error> originalErrors;

    private final String requestId;
    private final int statusCode;
    private final Map<String, String> responseHeaders;

    public SmartlingApiException(String message, Throwable cause, List<Error> originalErrors)
    {
        super(message, cause);
        this.originalErrors = originalErrors;
        this.requestId = HttpUtils.getRequestId().get() == null ? "N/A" : HttpUtils.getRequestId().get();

        HttpUtils.ResponseDetails responseDetails = HttpUtils.getResponseDetails().get();
        if (responseDetails != null)
        {
            this.statusCode = responseDetails.getStatusCode();
            this.responseHeaders = Collections.unmodifiableMap(convertHeadersToMap(responseDetails));
        }
        else
        {
            this.statusCode = 0;
            this.responseHeaders = Collections.emptyMap();
        }
    }

    private Map<String, String> convertHeadersToMap(final HttpUtils.ResponseDetails responseDetails)
    {
        Header[] headers = responseDetails.getHeaders();
        Map<String, String> headersMap = new HashMap<>(headers.length);
        for(Header header : headers)
        {
            headersMap.put(header.getName(), header.getValue());
        }
        return headersMap;
    }

    public SmartlingApiException(String message, List<Error> originalErrors)
    {
        this(message, null, originalErrors);
    }

    public SmartlingApiException(String message, Throwable cause)
    {
        this(message, cause, Collections.<Error>emptyList());
    }

    public SmartlingApiException(final Exception e)
    {
        this("", e, Collections.<Error>emptyList());
    }

    public SmartlingApiException(final String message)
    {
        this(message, null, Collections.<Error>emptyList());
    }

    public List<Error> getOriginalErrors()
    {
        return originalErrors;
    }

    public String getRequestId()
    {
        return requestId;
    }

    public int getStatusCode()
    {
        return statusCode;
    }

    public Map<String, String> getResponseHeaders()
    {
        return responseHeaders;
    }
}
