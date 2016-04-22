package com.smartling.api.sdk;

import com.smartling.api.sdk.auth.TokenProvider;
import com.smartling.api.sdk.dto.file.StringResponse;
import com.smartling.api.sdk.exceptions.SmartlingApiException;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpMessage;
import org.apache.http.client.methods.HttpRequestBase;

public class TokenProviderAwareClient extends BaseApiClient
{
    protected TokenProvider tokenProvider;

    protected StringResponse executeRequest(final HttpRequestBase request) throws SmartlingApiException
    {
        addAuthorizationHeader(request);
        addUserAgentHeader(request);
        return httpUtils.executeHttpCall(request, proxyConfiguration);
    }

    private void addAuthorizationHeader(final HttpMessage httpMessage) throws SmartlingApiException
    {
        httpMessage.addHeader(HttpHeaders.AUTHORIZATION, tokenProvider.getAuthenticationToken().getAuthorizationTokenString());
    }
}
