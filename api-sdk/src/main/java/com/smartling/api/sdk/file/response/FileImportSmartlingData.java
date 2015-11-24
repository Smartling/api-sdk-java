package com.smartling.api.sdk.file.response;

import com.smartling.api.sdk.dto.SmartlingData;
import com.smartling.web.api.v2.ResponseData;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class FileImportSmartlingData implements SmartlingData, ResponseData
{
    private int     stringCount;
    private int     wordCount;
    private List<FileImportError> translationImportErrors;

    public int getStringCount()
    {
        return stringCount;
    }

    public void setStringCount(int stringCount)
    {
        this.stringCount = stringCount;
    }

    public int getWordCount()
    {
        return wordCount;
    }

    public void setWordCount(int wordCount)
    {
        this.wordCount = wordCount;
    }

    public List<FileImportError> getTranslationImportErrors()
    {
        return translationImportErrors;
    }

    public void setTranslationImportErrors(List<FileImportError> translationImportErrors)
    {
        this.translationImportErrors = translationImportErrors;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("stringCount", getStringCount())
                .append("wordCount", getWordCount())
                .append("translationImportErrors", getTranslationImportErrors())
                .toString();
    }
}
