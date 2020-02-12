package com.smartling.api.sdk.file.response;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class UidAwareFileListItem extends FileListItem
{
    private String fileUid;

    /**
     * The identifier of the file.
     *
     * @return fileUid.
     */
    public String getFileUid()
    {
        return fileUid;
    }


    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("fileUri", getFileUri())
                .append("fileUid", getFileUid())
                .append("lastUploaded", getLastUploaded())
                .append("fileType", getFileType()).toString();
    }
}
