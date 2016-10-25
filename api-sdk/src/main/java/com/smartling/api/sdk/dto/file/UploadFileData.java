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
package com.smartling.api.sdk.dto.file;

import com.smartling.web.api.v2.ResponseData;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * The data returned from uploading a file to the Smartling Translation API.
 */
public class UploadFileData implements ResponseData
{
    private int     stringCount;
    private int     wordCount;
    private boolean overWritten;

    /**
     * The number of strings in the uploaded file.
     *
     * @return stringCount
     */
    public int getStringCount()
    {
        return stringCount;
    }

    /**
     * The number of words in the uploaded file.
     *
     * @return wordCount
     */
    public int getWordCount()
    {
        return wordCount;
    }

    /**
     * Returns whether or not the uploaded file was previously uploaded, i.e. was a file with the same fileUri previously uploaded.
     *
     * @return overWritten
     */
    public boolean isOverWritten()
    {
        return overWritten;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("stringCount", getStringCount())
                .append("wordCount", getWordCount())
                .append("overWritten", isOverWritten())
                .toString();
    }
}
