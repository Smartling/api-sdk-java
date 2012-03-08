package com.smartling.api.sdk.file.response;

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

}
