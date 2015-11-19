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

import java.util.List;

/**
 * Contains information about when a file was last modified
 */
public class FileLastModified implements ResponseData
{
    private List<FileLocaleLastModified> items;
    private int totalCount;

    public void setItems(final List<FileLocaleLastModified> items)
    {
        this.items = items;
    }

    public void setTotalCount(final int totalCount)
    {
        this.totalCount = totalCount;
    }

    /**
     * Get information about when a file was last modified for a particular locale
     *
     * @return list of {@link FileLocaleLastModified} entries
     */
    public List<FileLocaleLastModified> getItems()
    {
        return items;
    }

    public int getTotalCount()
    {
        return totalCount;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("totalCount: ", totalCount)
                .append("items", getItems())
                .toString();
    }
}
