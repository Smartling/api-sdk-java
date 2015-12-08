package com.smartling.api.sdk.auth;

import com.smartling.api.sdk.exceptions.SmartlingApiException;

public interface TokenProvider
{
    AuthenticationToken getAuthenticationToken() throws SmartlingApiException;
}
