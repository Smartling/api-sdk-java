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
 * limitations under the License. */
package com.smartling.api.sdk.file.response;

import java.util.List;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * The data expected back in the response to a FileList query of the Smartling Api.
 */
public class FileList implements Data
{
    private int              fileCount;
    private List<FileStatus> fileList;

    /**
     * Gets the number of files that match the query.
     *
     * @return fileCount that matches the query.
     */
    public int getFileCount()
    {
        return fileCount;
    }

    /**
     * Get the listing of files that match the query.
     *
     * @return {@link List} of {@link FileStatus}'s.
     */
    public List<FileStatus> getFileList()
    {
        return fileList;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("fileCount", getFileCount()).append("fileList", getFileList()).toString();

    }

}
