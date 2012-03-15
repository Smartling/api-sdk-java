package com.smartling.api.sdk.file.response;

import org.apache.commons.lang.builder.ToStringStyle;

import org.apache.commons.lang.builder.ToStringBuilder;

public class UploadData
{
    private int     stringCount;
    private int     wordCount;
    private boolean overWritten;

    public int getStringCount()
    {
        return stringCount;
    }

    public int getWordCount()
    {
        return wordCount;
    }

    public boolean isOverWritten()
    {
        return overWritten;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("stringCount", getStringCount())
                .append("wordCount", getWordCount())
                .append("overWritten", isOverWritten())
                .toString();
    }
}
