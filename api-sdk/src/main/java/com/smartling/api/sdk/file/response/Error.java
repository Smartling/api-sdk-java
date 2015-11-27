package com.smartling.api.sdk.file.response;

public class Error
{
    private String key;
    private String message;
    private ErrorDetails details;

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public ErrorDetails getDetails()
    {
        return details;
    }

    public void setDetails(ErrorDetails details)
    {
        this.details = details;
    }

    public Error (){};

    public Error(final String key, final String message, final ErrorDetails details)
    {
        this.key = key;
        this.message = message;
        this.details = details;
    }

    @Override public String toString()
    {
        return "Error{" +
                "key='" + key + '\'' +
                ", message='" + message + '\'' +
                ", details=" + details +
                '}';
    }
}