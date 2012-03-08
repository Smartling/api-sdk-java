package com.smartling.api.sdk.file.response;

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

}
