package com.smartling.api.sdk.file.response;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class FileListItem
{
    private String fileUri;
    private String lastUploaded;
    private String fileType;

    /**
     * The identifier of the file. It is recommended that the name of the file be used as the identifier.
     *
     * @return fileUri.
     */
    public String getFileUri()
    {
        return fileUri;
    }

    /**
     * The last time the file was uploaded to the Smartling Translation API.
     *
     * @return lastUploaded time
     */
    public String getLastUploaded()
    {
        return lastUploaded;
    }

    /**
     * The type of the file.
     *
     * @return fileType
     */
    public String getFileType()
    {
        return fileType;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("fileUri", getFileUri())
                .append("lastUploaded", getLastUploaded())
                .append("fileType", getFileType()).toString();
    }
}
