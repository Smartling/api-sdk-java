package com.smartling.api.sdk.auth;

public class AuthenticationToken
{
    private final String rawTokenString;

    public AuthenticationToken(final String tokenType, final String accessToken)
    {
        rawTokenString = String.format("%s %s", tokenType, accessToken);
    }

    public AuthenticationToken(final String rawTokenString)
    {
        this.rawTokenString = rawTokenString;
    }

    public String getAuthorizationTokenString()
    {
        return rawTokenString;
    }
}
