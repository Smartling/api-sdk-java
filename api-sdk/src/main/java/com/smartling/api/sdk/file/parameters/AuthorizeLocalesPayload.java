package com.smartling.api.sdk.file.parameters;

public class AuthorizeLocalesPayload
{
    private String fileUri;
    private String[] localeIds;

    public AuthorizeLocalesPayload(String fileUri, String[] localeIds)
    {
        this.fileUri = fileUri;
        this.localeIds = localeIds;
    }

    public String getFileUri()
    {
        return fileUri;
    }

    public String[] getLocaleIds()
    {
        return localeIds;
    }
}
