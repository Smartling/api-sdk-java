package com.smartling.api.sdk.auth;

import com.smartling.api.sdk.exceptions.SmartlingApiException;

import java.util.Objects;

public class ExistingTokenProvider implements TokenProvider
{
    private final AuthenticationToken authenticationToken;

    public ExistingTokenProvider(final AuthenticationToken authenticationToken)
    {
        this.authenticationToken = Objects.requireNonNull(authenticationToken, "Authentication token can not be null");
    }

    @Override
    public AuthenticationToken getAuthenticationToken() throws SmartlingApiException
    {
        return authenticationToken;
    }
}
