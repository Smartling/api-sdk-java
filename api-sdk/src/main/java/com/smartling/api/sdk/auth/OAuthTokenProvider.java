package com.smartling.api.sdk.auth;

import com.smartling.api.sdk.exceptions.SmartlingApiException;

public class OAuthTokenProvider implements TokenProvider
{
    private final String userId;
    private final String userSecret;
    private volatile AuthenticationContext authenticationContext;
    private final AuthApiClient authApiClient;

    public OAuthTokenProvider(final String userId, final String userSecret, final AuthApiClient authApiClient)
    {
        this.userId = userId;
        this.userSecret = userSecret;
        this.authApiClient = authApiClient;
    }

    @Override public AuthenticationToken getValidToken() throws SmartlingApiException
    {
        generateAuthenticationContext();
        return new AuthenticationToken(authenticationContext.getTokenType(), authenticationContext.getAccessToken());
    }

    void generateAuthenticationContext() throws SmartlingApiException
    {
        if (authenticationContext == null || System.currentTimeMillis() > authenticationContext.getAccessTokenExpireTime())
        {
            synchronized (this)
            {
                if (authenticationContext == null || System.currentTimeMillis() > authenticationContext.getAccessTokenExpireTime())
                {
                    if (authenticationContext == null || System.currentTimeMillis() > authenticationContext.getRefreshTokenExpireTime())
                    {
                        authenticationContext = authApiClient.authenticate(new AuthenticationCommand(userId, userSecret)).retrieveData();
                        authenticationContext.setParsingTime(System.currentTimeMillis());
                    }
                    else
                    {
                        authenticationContext = authApiClient.refresh(authenticationContext.getRefreshToken()).retrieveData();
                        authenticationContext.setParsingTime(System.currentTimeMillis());
                    }
                }
            }
        }
    }
}
