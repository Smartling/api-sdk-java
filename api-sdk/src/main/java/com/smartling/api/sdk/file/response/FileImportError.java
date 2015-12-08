package com.smartling.api.sdk.file.response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileImportError
{
    private String importKey;
    private String stringHashcode;
    private Long contentFileId;
    private List<String> messages;

    public FileImportError()
    {
    }

    public FileImportError(String importKey, String stringHashcode, Long contentFileId, String... messages)
    {
        this.importKey = importKey;
        this.stringHashcode = stringHashcode;
        this.contentFileId = contentFileId;
        if (null != messages)
            this.messages = new ArrayList<String>(Arrays.asList(messages));
    }

    public String getStringHashcode()
    {
        return stringHashcode;
    }

    public void setStringHashcode(String stringHashcode)
    {
        this.stringHashcode = stringHashcode;
    }

    public Long getContentFileId()
    {
        return contentFileId;
    }

    public void setContentFileId(Long contentFileId)
    {
        this.contentFileId = contentFileId;
    }

    public String getImportKey()
    {
        return importKey;
    }

    public void setImportKey(String importKey)
    {
        this.importKey = importKey;
    }

    public List<String> getMessages()
    {
        return messages;
    }

    public void setMessages(List<String> messages)
    {
        this.messages = messages;
    }
}
