package com.smartling.api.sdk;

import com.smartling.api.sdk.auth.AuthApiClient;
import com.smartling.api.sdk.auth.AuthenticationCommand;
import com.smartling.api.sdk.exceptions.SmartlingApiException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AuthenticationApiIntegrationTest
{
    private AuthApiClient authApiClient;

    @Before
    public void setUp() throws Exception
    {
        authApiClient = new AuthApiClient();
    }

    @Test
    public void testRefreshToken() throws SmartlingApiException
    {
        String refreshToken = authApiClient.authenticate(new AuthenticationCommand(System.getProperty("userId"), System.getProperty("userSecret"))).getData().getRefreshToken();

        assertTrue(authApiClient.refresh(refreshToken).getData().getAccessToken().length() > 10);
    }
}
