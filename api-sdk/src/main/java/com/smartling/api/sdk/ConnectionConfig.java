package com.smartling.api.sdk;

public class ConnectionConfig
{
    private ProxyConfiguration proxyConfiguration;
    private String baseFileApiUrl;
    private String projectId;

    public ConnectionConfig(final ProxyConfiguration proxyConfiguration, final String baseFileApiUrl, final String projectId)
    {
        this.proxyConfiguration = proxyConfiguration;
        this.baseFileApiUrl = baseFileApiUrl;
        this.projectId = projectId;
    }

    public void setProxyConfiguration(final ProxyConfiguration proxyConfiguration)
    {
        this.proxyConfiguration = proxyConfiguration;
    }

    public ProxyConfiguration getProxyConfiguration()
    {
        return proxyConfiguration;
    }

    public String getBaseFileApiUrl()
    {
        return baseFileApiUrl;
    }

    public String getProjectId()
    {
        return projectId;
    }

    public void setBaseFileApiUrl(final String baseFileApiUrl)
    {
        this.baseFileApiUrl = baseFileApiUrl;
    }

    public void setProjectId(final String projectId)
    {
        this.projectId = projectId;
    }
}
