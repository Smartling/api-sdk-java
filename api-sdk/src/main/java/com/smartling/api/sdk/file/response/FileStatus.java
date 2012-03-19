/*
 * Copyright 2012 Smartling, Inc.
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

import org.apache.commons.lang.builder.ToStringStyle;

import org.apache.commons.lang.builder.ToStringBuilder;

public class FileStatus
{
    private String fileUri;
    private int    stringCount;
    private int    wordCount;
    private int    approvedStringCount;
    private int    completedStringCount;
    private String lastUploaded;
    private String fileType;

    public String getFileUri()
    {
        return fileUri;
    }

    public int getStringCount()
    {
        return stringCount;
    }

    public int getWordCount()
    {
        return wordCount;
    }

    public int getApprovedStringCount()
    {
        return approvedStringCount;
    }

    public int getCompletedStringCount()
    {
        return completedStringCount;
    }

    public String getLastUploaded()
    {
        return lastUploaded;
    }

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
                .append("fileType", getFileType())
                .toString();

    }
}
