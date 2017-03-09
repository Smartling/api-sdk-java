package com.smartling.api.sdk.auth;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class AuthenticationContextTest
{
    @Test
    public void testGetAccessTokenRefreshTime() throws Exception
    {
        AuthenticationContext context = new AuthenticationContext();
        context.setParsingTime(100000);
        context.setExpiresIn(1);

        long tokenExpireTime = context.calculateAccessTokenExpireTime();

        assertThat(tokenExpireTime, equalTo(98000L));
    }

    @Test
    public void testGetRefreshTokenRefreshTime() throws Exception
    {
        AuthenticationContext context = new AuthenticationContext();
        context.setParsingTime(100000);
        context.setRefreshExpiresIn(10);

        long tokenExpireTime = context.calculateRefreshTokenExpireTime();

        assertThat(tokenExpireTime, equalTo(107000L));
    }
}