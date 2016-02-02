package com.smartling.api.sdk.auth;

import com.smartling.api.sdk.exceptions.SmartlingApiException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;

public class OAuthTokenProvider implements TokenProvider
{
    private final String userId;
    private final String userSecret;
    private final AuthApiClient authApiClient;
    private static final Log LOGGER = LogFactory.getLog(OAuthTokenProvider.class);

    private volatile AuthenticationContext authenticationContext;

    public OAuthTokenProvider(final String userId, final String userSecret, final AuthApiClient authApiClient)
    {
        this.userId = userId;
        this.userSecret = userSecret;
        this.authApiClient = authApiClient;
    }

    @Override public AuthenticationToken getAuthenticationToken() throws SmartlingApiException
    {
        generateAuthenticationContext();
        return new AuthenticationToken(authenticationContext.getTokenType(), authenticationContext.getAccessToken());
    }

    private void generateAuthenticationContext() throws SmartlingApiException
    {
        if (accessTokenIsNotValid())
        {
            synchronized (authApiClient)
            {
                if (accessTokenIsNotValid())
                {
                    if (refreshTokenIsValid())
                    {
                        final String refreshToken = authenticationContext.getRefreshToken();
                        try
                        {
                            authenticationContext = authApiClient.refresh(refreshToken).retrieveData();
                        } catch (SmartlingApiException ex){
                            authenticationContext = null;
                            LOGGER.warn("Reset refresh token after fail");
                            JwtConsumer consumer = new JwtConsumerBuilder()
                                    .setSkipAllDefaultValidators()
                                    .setRequireExpirationTime()
                                    .setSkipSignatureVerification()
                                    .build();
                            try
                            {
                                LOGGER.warn("Failed token info: " + consumer.process(refreshToken).getJwtClaims().getRawJson());
                            }
                            catch (InvalidJwtException e)
                            {
                                LOGGER.warn("Can't parse refresh token");
                            }
                            throw ex;
                        }
                    }
                    else
                    {
                        authenticationContext = authApiClient.authenticate(new AuthenticationCommand(userId, userSecret)).retrieveData();
                    }
                }
            }
        }
    }

    private boolean refreshTokenIsValid()
    {
        return authenticationContext != null && System.currentTimeMillis() <= authenticationContext.calculateRefreshTokenExpireTime();
    }

    private boolean accessTokenIsNotValid()
    {
        return authenticationContext == null || System.currentTimeMillis() > authenticationContext.calculateAccessTokenExpireTime();
    }
}
