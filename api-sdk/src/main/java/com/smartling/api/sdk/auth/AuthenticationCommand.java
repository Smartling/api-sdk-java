package com.smartling.api.sdk.auth;

public class AuthenticationCommand
{
    private final String userIdentifier;
    private final String userSecret;

    public AuthenticationCommand(String userIdentifier, String userSecret)
    {
        this.userIdentifier = userIdentifier;
        this.userSecret = userSecret;
    }

    public String getUserIdentifier()
    {
        return userIdentifier;
    }

    public String getUserSecret()
    {
        return userSecret;
    }

}
