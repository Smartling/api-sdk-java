package com.smartling.api.sdk.file;

class FileDeleteCommand
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

    public static class Builder
    {
        private String fileUri;

        public Builder setFileUri(String fileUri)
        {
            this.fileUri = fileUri;
            return this;
        }

        public FileDeleteCommand build()
        {
            return new FileDeleteCommand(fileUri);
        }
    }
}
