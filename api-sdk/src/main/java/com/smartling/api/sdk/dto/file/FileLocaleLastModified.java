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

import com.smartling.api.sdk.dto.SmartlingData;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * Contains information about when a file was last modified for a particular localeId
 */
public class FileLocaleLastModified implements SmartlingData
{
    private String localeId;
    private Date lastModified;

    /**
     * Get localeId identifier
     *
     * @return localeId identifier
     */
    public String getLocaleId()
    {
        return localeId;
    }

    /**
     * Get date when a file was last modified
     *
     * @return date when a file was last modified
     */
    public Date getLastModified()
    {
        return lastModified;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("localeId", getLocaleId())
                .append("lastModified", getLastModified())
                .toString();
    }
}
