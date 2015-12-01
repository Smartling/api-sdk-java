package com.smartling.api.sdk.auth;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AuthenticationTokenTest
{

    @Test
    public void testGetAuthorizationTokenString() throws Exception
    {
        AuthenticationToken token = new AuthenticationToken("type", "accessToken");
        assertEquals("type accessToken", token.getAuthorizationTokenString());
    }
}