package com.smartling.api.sdk.auth;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AuthenticationContextTest
{

    @Test
    public void testGetAccessTokenRefreshTime() throws Exception
    {
        AuthenticationContext context = new AuthenticationContext();
        context.setParsingTime(100000);
        context.setExpiresIn(1);
        assertEquals(99500, context.getAccessTokenExpireTime());
    }

    @Test
    public void testGetRefreshTokenRefreshTime() throws Exception
    {
        AuthenticationContext context = new AuthenticationContext();
        context.setParsingTime(100000);
        context.setRefreshExpiresIn(10);
        assertEquals(108500, context.getRefreshTokenExpireTime());
    }
}