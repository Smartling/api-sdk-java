package com.smartling.api.sdk.file.response;

import com.smartling.api.sdk.dto.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class FileStatusItem implements Data
{
    private String localeId;
    private int    authorizedStringCount;
    private int    authorizedWordCount;
    private int    completedStringCount;
    private int    completedWordCount;
    private int    excludedStringCount;
    private int    excludedWordCount;

    public String getLocaleId()
    {
        return localeId;
    }

    public int getAuthorizedStringCount()
    {
        return authorizedStringCount;
    }

    public int getAuthorizedWordCount()
    {
        return authorizedWordCount;
    }

    public int getCompletedStringCount()
    {
        return completedStringCount;
    }

    public int getCompletedWordCount()
    {
        return completedWordCount;
    }

    public int getExcludedStringCount()
    {
        return excludedStringCount;
    }

    public int getExcludedWordCount()
    {
        return excludedWordCount;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("localeId", getLocaleId())
                .append("authorizedStringCount", getAuthorizedStringCount())
                .append("authorizedWordCount", getAuthorizedWordCount())
                .append("completedStringCount", getCompletedStringCount())
                .append("completedWordCount", getCompletedWordCount())
                .append("excludedStringCount", getExcludedStringCount())
                .append("excludedWordCount", getExcludedWordCount())
                .toString();
    }
}
