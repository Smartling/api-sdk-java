package com.smartling.api.sdk.file.response;

public class FileStatus
{
    private String fileUri;
    private int    stringCount;
    private int    wordCount;
    private int    approvedStringCount;
    private int    completedStringCount;
    private String lastUploaded;
    private String fileType;

    public String getFileUri()
    {
        return fileUri;
    }

    public int getStringCount()
    {
        return stringCount;
    }

    public int getWordCount()
    {
        return wordCount;
    }

    public int getApprovedStringCount()
    {
        return approvedStringCount;
    }

    public int getCompletedStringCount()
    {
        return completedStringCount;
    }

    public String getLastUploaded()
    {
        return lastUploaded;
    }

    public String getFileType()
    {
        return fileType;
    }

}
