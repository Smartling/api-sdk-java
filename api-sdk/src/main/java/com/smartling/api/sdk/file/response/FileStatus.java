package com.smartling.api.sdk.file.response;

import com.smartling.web.api.v2.ResponseData;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class FileStatus implements ResponseData
{
    private String fileUri;
    private String lastUploaded;
    private String fileType;
    private int    totalStringCount;
    private int    totalWordCount;
    private int    totalCount;
    private List<FileStatusItem> items;

    public String getFileUri()
    {
        return fileUri;
    }

    public String getLastUploaded()
    {
        return lastUploaded;
    }

    public String getFileType()
    {
        return fileType;
    }

    public int getTotalStringCount()
    {
        return totalStringCount;
    }

    public int getTotalWordCount()
    {
        return totalWordCount;
    }

    public int getTotalCount()
    {
        return totalCount;
    }

    public List<FileStatusItem> getItems()
    {
        return items;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("fileUri", getFileUri())
                .append("totalStringCount", getTotalStringCount())
                .append("totalWordCount", getTotalWordCount())
                .append("lastUploaded", getLastUploaded())
                .append("fileType", getFileType())
                .append("totalCount", getTotalCount())
                .append("items", getItems())
                .toString();
    }
}
