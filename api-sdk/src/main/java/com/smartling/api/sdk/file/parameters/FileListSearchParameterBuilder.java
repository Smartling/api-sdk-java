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
package com.smartling.api.sdk.file.parameters;

import com.smartling.api.sdk.file.FileType;

import java.util.Date;
import java.util.List;

/**
 * Contains the search parameters that can be set when executing a file/list query through the Smartling Translation API.
 */
public class FileListSearchParameterBuilder
{
    private String       uriMask;
    private List<FileType> fileTypes;
    private Date lastUploadedAfter;
    private Date         lastUploadedBefore;
    private Integer      offset;
    private Integer      limit;

    public String getUriMask()
    {
        return uriMask;
    }

    public FileListSearchParameterBuilder withUriMask(String uriMask)
    {
        this.uriMask = uriMask;
        return this;
    }

    public List<FileType> getFileTypes()
    {
        return fileTypes;
    }

    public FileListSearchParameterBuilder withFileTypes(List<FileType> fileTypes)
    {
        this.fileTypes = fileTypes;
        return this;
    }

    public Date getLastUploadedAfter()
    {
        return lastUploadedAfter;
    }

    public FileListSearchParameterBuilder withLastUploadedAfter(Date lastUploadedAfter)
    {
        this.lastUploadedAfter = lastUploadedAfter;
        return this;
    }

    public Date getLastUploadedBefore()
    {
        return lastUploadedBefore;
    }

    public FileListSearchParameterBuilder withLastUploadedBefore(Date lastUploadedBefore)
    {
        this.lastUploadedBefore = lastUploadedBefore;
        return this;
    }

    public Integer getOffset()
    {
        return offset;
    }

    public FileListSearchParameterBuilder withOffset(Integer offset)
    {
        this.offset = offset;
        return this;
    }

    public Integer getLimit()
    {
        return limit;
    }

    public FileListSearchParameterBuilder withLimit(Integer limit)
    {
        this.limit = limit;
        return this;
    }
}
