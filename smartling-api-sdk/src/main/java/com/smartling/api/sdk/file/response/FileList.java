package com.smartling.api.sdk.file.response;

import org.apache.commons.lang.builder.ToStringStyle;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;

public class FileList
{
    private int              fileCount;
    private List<FileStatus> fileList;

    public int getFileCount()
    {
        return fileCount;
    }

    public List<FileStatus> getFileList()
    {
        return fileList;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("fileCount", getFileCount())
                .append("fileList", getFileList())
                .toString();

    }

}
