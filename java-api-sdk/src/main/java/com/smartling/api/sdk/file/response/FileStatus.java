package com.smartling.api.sdk.file.response;

import org.apache.commons.lang.builder.ToStringStyle;

import org.apache.commons.lang.builder.ToStringBuilder;

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

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("fileUri", getFileUri())
                .append("stringCount", getStringCount())
                .append("wordCount", getWordCount())
                .append("approvedStringCount", getApprovedStringCount())
                .append("completedStringCount", getCompletedStringCount())
                .append("lastUploaded", getLastUploaded())
                .append("fileType", getFileType())
                .toString();

    }
}
