package com.smartling.api.sdk.file.response;

import org.apache.commons.lang.builder.ToStringStyle;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;

public class ApiResponse<T>
{
    private T            data;
    private String       code;
    private List<String> messages;

    public T getData()
    {
        return data;
    }

    public String getCode()
    {
        return code;
    }

    public List<String> getMessages()
    {
        return messages;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("data", getData())
                .append("code", getCode())
                .append("messages", messages)
                .toString();

    }
}
