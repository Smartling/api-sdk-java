package com.smartling.api.sdk;

public class ConnectionConfig
{
    private final ProxyConfiguration proxyConfiguration;
    private final String baseFileApiUrl;
    private final String projectId;

    public ConnectionConfig(final ProxyConfiguration proxyConfiguration, final String baseFileApiUrl, final String projectId)
    {
        this.proxyConfiguration = proxyConfiguration;
        this.baseFileApiUrl = baseFileApiUrl;
        this.projectId = projectId;
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
}
