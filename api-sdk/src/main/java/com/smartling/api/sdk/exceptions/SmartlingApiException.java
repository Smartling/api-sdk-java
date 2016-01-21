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

import java.util.List;
public class SmartlingApiException extends Exception
{
    private static final long serialVersionUID = -397098626101615761L;

    private List<Error> originalErrors;

    private String requestId;

    public SmartlingApiException(final String contents, List<Error> originalErrors)
    {
        super(contents);
        this.originalErrors = originalErrors;
        setRequestId();
    }

    public SmartlingApiException(final Exception e)
    {
        super(e);
        setRequestId();
    }

    public List<Error> getOriginalErrors()
    {
        return originalErrors;
    }

    public String getRequestId()
    {
        return requestId;
    }

    private void setRequestId()
    {
        this.requestId = HttpUtils.requestId.get() == null ? "not available" : HttpUtils.requestId.get();
    }

}
