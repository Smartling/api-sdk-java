package com.smartling.api.sdk.projects.response;

import com.smartling.web.api.v2.ResponseData;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class ProjectDetails implements ResponseData
{
    private String  projectId;
    private String  projectName;
    private String  accountUid;
    private boolean archived;
    private String  projectTypeCode;
    private String  sourceLocaleId;
    private String  sourceLocaleDescription;
    private List<ProjectLocale> targetLocales;

    public String getProjectId()
    {
        return projectId;
    }

    public String getProjectName()
    {
        return projectName;
    }

    public String getAccountUid()
    {
        return accountUid;
    }

    public boolean isArchived()
    {
        return archived;
    }

    public String getProjectTypeCode()
    {
        return projectTypeCode;
    }

    public String getSourceLocaleId()
    {
        return sourceLocaleId;
    }

    public String getSourceLocaleDescription()
    {
        return sourceLocaleDescription;
    }

    public List<ProjectLocale> getTargetLocales()
    {
        return targetLocales;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("projectId", projectId)
                .append("projectName", projectName)
                .append("accountUid", accountUid)
                .append("archived", archived)
                .append("projectTypeCode", projectTypeCode)
                .append("sourceLocaleId", sourceLocaleId)
                .append("sourceLocaleDescription", sourceLocaleDescription)
                .append("targetLocales", targetLocales)
                .toString();
    }
}
