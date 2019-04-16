package com.smartling.api.sdk;

/**
 * All timeouts are in milliseconds.  The defaults here are designed
 * for a service with a relatively low number of long running requests.
 */
public class HttpClientConfiguration
{
    private int     connectionRequestTimeout    = 60000;
    private int     connectionTimeout           = 10000;
    private int     socketTimeout               = 10000;

    public int getConnectionRequestTimeout()
    {
        return connectionRequestTimeout;
    }

    public HttpClientConfiguration setConnectionRequestTimeout(final int connectionRequestTimeout)
    {
        this.connectionRequestTimeout = connectionRequestTimeout;
        return this;
    }

    public int getConnectionTimeout()
    {
        return connectionTimeout;
    }

    public HttpClientConfiguration setConnectionTimeout(final int connectionTimeout)
    {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public int getSocketTimeout()
    {
        return socketTimeout;
    }

    public HttpClientConfiguration setSocketTimeout(final int socketTimeout)
    {
        this.socketTimeout = socketTimeout;
        return this;
    }

}
