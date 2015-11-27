package com.smartling.api.sdk.file.parameters;

public class FileDeletePayload
{
    private String fileUri;

    public FileDeletePayload(String fileUri)
    {
        this.fileUri = fileUri;
    }

    public String getFileUri()
    {
        return fileUri;
    }

    public void setFileUri(String fileUri)
    {
        this.fileUri = fileUri;
    }

}
