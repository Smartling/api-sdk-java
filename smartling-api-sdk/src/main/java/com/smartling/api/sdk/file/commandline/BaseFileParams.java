package com.smartling.api.sdk.file.commandline;

public class BaseFileParams
{
    private String baseApiUrl;
    private String apiKey;
    private String projectId;

    public String getBaseApiUrl()
    {
        return baseApiUrl;
    }

    public void setBaseApiUrl(String baseApiUrl)
    {
        this.baseApiUrl = baseApiUrl;
    }

    public String getApiKey()
    {
        return apiKey;
    }

    public void setApiKey(String apiKey)
    {
        this.apiKey = apiKey;
    }

    public String getProjectId()
    {
        return projectId;
    }

    public void setProjectId(String projectId)
    {
        this.projectId = projectId;
    }

}
