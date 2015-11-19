package com.smartling.api.sdk.file;

public class AuthorizeLocalesCommand
{
    private String fileUri;
    private String[] localeIds;

    public AuthorizeLocalesCommand(String fileUri, String[] localeIds)
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

    public static class Builder
    {
        private String fileUri;
        private String[] localeIds;

        public Builder setFileUri(String fileUri)
        {
            this.fileUri = fileUri;
            return this;
        }

        public Builder setLocaleIds(String[] localeIds)
        {
            this.localeIds = localeIds;
            return this;
        }

        public AuthorizeLocalesCommand build()
        {
            return new AuthorizeLocalesCommand(fileUri, localeIds);
        }
    }
}
