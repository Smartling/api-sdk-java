package com.smartling.api.sdk;

import com.smartling.api.sdk.auth.AuthenticationToken;
import com.smartling.api.sdk.auth.TokenProvider;
import com.smartling.api.sdk.dto.file.StringResponse;
import com.smartling.api.sdk.exceptions.SmartlingApiException;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpRequestBase;

import java.util.Objects;

public abstract class TokenProviderAwareClient extends BaseApiClient
{
    private final TokenProvider tokenProvider;

    protected TokenProviderAwareClient(final String baseUrl, final ProxyConfiguration proxyConfiguration, TokenProvider tokenProvider)
    {
        super(baseUrl, proxyConfiguration);
        this.tokenProvider = Objects.requireNonNull(tokenProvider, "Token Provider can not be null");
    }

    protected StringResponse executeRequest(final HttpRequestBase request) throws SmartlingApiException
    {
        AuthenticationToken authenticationToken = tokenProvider.getAuthenticationToken();
        request.addHeader(HttpHeaders.AUTHORIZATION, authenticationToken.getAuthorizationTokenString());

        return super.executeRequest(request);
    }
}
