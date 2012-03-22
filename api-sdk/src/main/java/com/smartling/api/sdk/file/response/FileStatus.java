/* Copyright 2012 Smartling, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this work except in compliance with the License.
 * You may obtain a copy of the License in the LICENSE file, or at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.smartling.api.sdk.file.response;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * The status of a particular file managed by the Smartling Translation API.
 */
public class FileStatus implements Data
{
    private String fileUri;
    private int    stringCount;
    private int    wordCount;
    private int    approvedStringCount;
    private int    completedStringCount;
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
     * The number of strings contained in the file.
     *
     * @return stringCount of file.
     */
    public int getStringCount()
    {
        return stringCount;
    }

    /**
     * The number of words contained in the file.
     *
     * @return wordCount of file.
     */
    public int getWordCount()
    {
        return wordCount;
    }

    /**
     * The number of approved strings in the file.
     *
     * @return approvedStringCount
     */
    public int getApprovedStringCount()
    {
        return approvedStringCount;
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
            .append("stringCount", getStringCount())
            .append("wordCount", getWordCount())
            .append("approvedStringCount", getApprovedStringCount())
            .append("completedStringCount", getCompletedStringCount())
            .append("lastUploaded", getLastUploaded())
            .append("fileType", getFileType()).toString();
    }
}
