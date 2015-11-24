package com.smartling.api.sdk.file.parameters;

public class FileDeleteCommand
{
    private String fileUri;

    public FileDeleteCommand(String fileUri)
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
