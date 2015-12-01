package com.smartling.api.sdk.auth;

import com.smartling.api.sdk.exceptions.SmartlingApiException;

public class ExistingTokenProvider implements TokenProvider
{
    private final AuthenticationToken authenticationToken;

    public ExistingTokenProvider(final AuthenticationToken authenticationToken)
    {
        this.authenticationToken = authenticationToken;
    }

    @Override public AuthenticationToken getValidToken() throws SmartlingApiException
    {
        return authenticationToken;
    }

}
