package com.smartling.api.sdk.file.response;

import org.apache.commons.lang.builder.ToStringStyle;

import org.apache.commons.lang.builder.ToStringBuilder;

public class UploadData
{
    private int     stringCount;
    private int     wordCount;
    private boolean overwritten;

    public int getStringCount()
    {
        return stringCount;
    }

    public int getWordCount()
    {
        return wordCount;
    }

    public boolean isOverwritten()
    {
        return overwritten;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("stringCount", getStringCount())
                .append("wordCount", getWordCount())
                .append("overwritten", isOverwritten())
                .toString();
    }
}
