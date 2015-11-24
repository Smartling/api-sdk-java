package com.smartling.api.sdk.file.response;

import com.smartling.api.sdk.dto.SmartlingData;
import com.smartling.web.api.v2.ResponseData;

import java.util.List;

public class AuthorizedLocales implements SmartlingData, ResponseData
{
    private List<String> items;

    public List<String> getItems()
    {
        return items;
    }

    public void setItems(List<String> items)
    {
        this.items = items;
    }
}
