package com.smartling.api.sdk.auth;

public class AuthenticationToken
{
    private final String tokenType;
    private final String accessToken;

    public AuthenticationToken(final String tokenType, final String accessToken)
    {
        this.tokenType = tokenType;
        this.accessToken = accessToken;
    }

    public String getAuthorizationTokenString()
    {
        return String.format("%s %s", tokenType, accessToken);
    }

}
