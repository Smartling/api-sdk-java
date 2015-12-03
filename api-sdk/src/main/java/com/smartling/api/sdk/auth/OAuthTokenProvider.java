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

    private void generateAuthenticationContext() throws SmartlingApiException
    {
        if (newAccessTokenNeeded())
        {
            synchronized (authApiClient)
            {
                if (newAccessTokenNeeded())
                {
                    if (canUseRefreshToken())
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

    private boolean canUseRefreshToken()
    {
        return authenticationContext != null && System.currentTimeMillis() <= authenticationContext.getRefreshTokenExpireTime();
    }

    private boolean newAccessTokenNeeded()
    {
        return authenticationContext == null || System.currentTimeMillis() > authenticationContext.getAccessTokenExpireTime();
    }
}
