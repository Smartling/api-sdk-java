package com.smartling.api.sdk.file.response;

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

}
