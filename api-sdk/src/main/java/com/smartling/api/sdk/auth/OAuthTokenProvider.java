package com.smartling.api.sdk.auth;

import com.smartling.api.sdk.exceptions.SmartlingApiException;

public class OAuthTokenProvider implements TokenProvider
{
    private final String userId;
    private final String userSecret;
    private final AuthApiClient authApiClient;

    private volatile AuthenticationContext authenticationContext;

    public OAuthTokenProvider(final String userId, final String userSecret, final AuthApiClient authApiClient)
    {
        this.userId = userId;
        this.userSecret = userSecret;
        this.authApiClient = authApiClient;
    }

    @Override public AuthenticationToken getAuthenticationToken() throws SmartlingApiException
    {
        generateAuthenticationContext();
        return new AuthenticationToken(authenticationContext.getTokenType(), authenticationContext.getAccessToken());
    }

    private void generateAuthenticationContext() throws SmartlingApiException
    {
        if (accessTokenIsNotValid())
        {
            synchronized (authApiClient)
            {
                if (accessTokenIsNotValid())
                {
                    if (refreshTokenIsValid())
                    {
                        authenticationContext = authApiClient.refresh(authenticationContext.getRefreshToken()).retrieveData();
                    }
                    else
                    {
                        authenticationContext = authApiClient.authenticate(new AuthenticationCommand(userId, userSecret)).retrieveData();
                    }
                }
            }
        }
    }

    private boolean refreshTokenIsValid()
    {
        return authenticationContext != null && System.currentTimeMillis() <= authenticationContext.calculateRefreshTokenExpireTime();
    }

    private boolean accessTokenIsNotValid()
    {
        return authenticationContext == null || System.currentTimeMillis() > authenticationContext.calculateAccessTokenExpireTime();
    }
}
