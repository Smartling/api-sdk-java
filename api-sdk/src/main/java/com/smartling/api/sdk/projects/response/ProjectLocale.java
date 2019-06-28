package com.smartling.api.sdk.projects.response;

import com.smartling.api.sdk.dto.SmartlingData;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ProjectLocale implements SmartlingData
{

    private String localeId;
    private String description;
    private boolean enabled;

    public String getLocaleId()
    {
        return localeId;
    }

    public String getDescription()
    {
        return description;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("localeId", localeId)
                .append("description", description)
                .append("enabled", enabled)
                .toString();
    }
}
