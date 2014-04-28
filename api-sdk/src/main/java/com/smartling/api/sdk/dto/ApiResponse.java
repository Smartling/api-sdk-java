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
package com.smartling.api.sdk.dto;

import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Base response class returned from the Smartling Translation API.
 *
 * @param <T> The type of the data class expected in the return.
 */
public class ApiResponse<T extends Data>
{
    private T            data;
    private String       code;
    private List<String> messages;

    /**
     * The data returned from the response from the Smartling Translation API.
     * The data varies by api call.
     *
     * @return data from the json response.
     */
    public T getData()
    {
        return data;
    }

    /**
     * The response code returned from the Smartling Translation API.
     * @return response code
     */
    public String getCode()
    {
        return code;
    }

    /**
     * The messages returned form the Smartling Translation API.
     * @return list of messages.
     */
    public List<String> getMessages()
    {
        return messages;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("data", getData()).append("code", getCode()).append("messages", messages).toString();
    }
}
