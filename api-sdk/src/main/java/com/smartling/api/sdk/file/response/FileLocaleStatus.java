package com.smartling.api.sdk.file.response;

import com.smartling.web.api.v2.ResponseData;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class FileLocaleStatus implements ResponseData
{
    private String fileUri;
    private String lastUploaded;
    private String fileType;
    private int    totalStringCount;
    private int    totalWordCount;
    private int    authorizedStringCount;
    private int    authorizedWordCount;
    private int    completedStringCount;
    private int    completedWordCount;
    private int    excludedStringCount;
    private int    excludedWordCount;

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
     * The number of strings contained in the file.
     *
     * @return totalStringCount of file.
     */
    public int getTotalStringCount()
    {
        return totalStringCount;
    }

    /**
     * The number of words contained in the file.
     *
     * @return totalWordCount of file.
     */
    public int getTotalWordCount()
    {
        return totalWordCount;
    }

    /**
     * The number of authorized strings in the file.
     *
     * @return authorizedStringCount
     */
    public int getAuthorizedStringCount()
    {
        return authorizedStringCount;
    }

    /**
     * The number of authorized words in the file.
     *
     * @return authorizedWordCount
     */
    public int getAuthorizedWordCount()
    {
        return authorizedWordCount;
    }

    /**
     * The number of completed strings in the file.
     *
     * @return completedStringCount
     */
    public int getCompletedStringCount()
    {
        return completedStringCount;
    }

    /**
     * The number of completed words in the file.
     *
     * @return completedWordCount
     */
    public int getCompletedWordCount()
    {
        return completedWordCount;
    }

    /**
     * The number of excluded strings in the file.
     *
     * @return excludedStringCount
     */
    public int getExcludedStringCount()
    {
        return excludedStringCount;
    }

    /**
     * The number of excluded words in the file.
     *
     * @return excludedWordCount
     */
    public int getExcludedWordCount()
    {
        return excludedWordCount;
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
                .append("totalStringCount", getTotalStringCount())
                .append("totalWordCount", getTotalWordCount())
                .append("authorizedStringCount", getAuthorizedStringCount())
                .append("authorizedWordCount", getAuthorizedWordCount())
                .append("completedStringCount", getCompletedStringCount())
                .append("completedWordCount", getCompletedWordCount())
                .append("excludedStringCount", getExcludedStringCount())
                .append("excludedWordCount", getExcludedWordCount())
                .append("lastUploaded", getLastUploaded())
                .append("fileType", getFileType()).toString();
    }
}
