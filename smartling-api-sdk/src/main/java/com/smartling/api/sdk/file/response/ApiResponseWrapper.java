package com.smartling.api.sdk.file.response;

import org.apache.commons.lang.builder.ToStringStyle;

import org.apache.commons.lang.builder.ToStringBuilder;

public class ApiResponseWrapper<T>
{
    private ApiResponse<T> response;

    public ApiResponse<T> getResponse()
    {
        return response;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("response", getResponse()).toString();
    }
}
