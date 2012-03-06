package com.smartling.api.sdk.file;

public class FileApiException extends Exception
{
    public FileApiException(String message)
    {
        super(message);
    }

    public FileApiException(Exception e)
    {
        super(e);
    }
}
