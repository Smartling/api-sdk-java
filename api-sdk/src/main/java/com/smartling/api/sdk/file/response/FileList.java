package com.smartling.api.sdk.file.response;

import com.smartling.api.sdk.dto.SmartlingData;
import com.smartling.web.api.v2.ResponseData;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class FileList<Item extends FileListItem> implements SmartlingData, ResponseData
{
    private int              totalCount;
    private List<Item>       items;

    /**
     * Gets the number of files that match the query.
     *
     * @return fileCount that matches the query.
     */
    public int getTotalCount()
    {
        return totalCount;
    }

    /**
     * Get the listing of files that match the query.
     *
     * @return {@link java.util.List} of items.
     */
    public List<Item> getItems()
    {
        return items;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("totalCount", getTotalCount()).append("items", getItems()).toString();
    }
}
