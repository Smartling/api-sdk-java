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

import java.util.ArrayList;
import java.util.List;

/**
 * Thrown when an exception has occurred when using the {@link com.smartling.api.sdk.FileApiClientAdapter}.
 */
public class ApiException extends Exception
{
    private static final long serialVersionUID = -397098626101615761L;

    private List<String> messages = new ArrayList<>();

    public ApiException(final String contents, List<String> messages)
    {
        super(contents);
        this.messages = messages;
    }

    public ApiException(final Exception e)
    {
        super(e);
        messages.add(e.getMessage());
    }

    public List<String> getMessages()
    {
        return messages;
    }
}
