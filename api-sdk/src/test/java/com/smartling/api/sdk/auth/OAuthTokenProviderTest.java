package com.smartling.api.sdk.auth;

import com.smartling.api.sdk.file.response.Response;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OAuthTokenProviderTest
{

    @Test
    public void testGenerateAuthenticationContext() throws Exception
    {
        AuthApiClient authApiClient = mock(AuthApiClient.class);
        OAuthTokenProvider oAuthTokenProvider = new OAuthTokenProvider("userId", "userSecret", authApiClient);
        Response<AuthenticationContext> response = new Response<>();
        AuthenticationContext context = new AuthenticationContext();
        context.setExpiresIn(1);
        context.setAccessToken("111");
        context.setRefreshToken("222");
        response.setData(context);
        when(authApiClient.authenticate(any(AuthenticationCommand.class))).thenReturn(response);
        when(authApiClient.refresh(anyString())).thenReturn(response);

        oAuthTokenProvider.getValidToken();
        verify(authApiClient).authenticate(new AuthenticationCommand("userId","userSecret"));
        context.setRefreshExpiresIn(1000);
        oAuthTokenProvider.getValidToken();
        verify(authApiClient).refresh("222");
        context.setRefreshExpiresIn(1);
        verify(authApiClient).authenticate(new AuthenticationCommand("userId","userSecret"));
    }
}