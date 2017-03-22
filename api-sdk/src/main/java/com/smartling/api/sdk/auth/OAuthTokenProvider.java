package com.smartling.api.sdk.auth;

import com.smartling.api.sdk.exceptions.SmartlingApiException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;

import java.util.Objects;

public class OAuthTokenProvider implements TokenProvider
{
    private static final Log LOGGER = LogFactory.getLog(OAuthTokenProvider.class);

    private final String userId;
    private final String userSecret;
    private final AuthApiClient authApiClient;

    private volatile AuthenticationContext authenticationContext;

    public OAuthTokenProvider(final String userId, final String userSecret, final AuthApiClient authApiClient)
    {
        this.userId = Objects.requireNonNull(userId, "User ID can not be null");
        this.userSecret = Objects.requireNonNull(userSecret, "User Secret can not be null");
        this.authApiClient = Objects.requireNonNull(authApiClient, "Auth. API client can not be null");
    }

    @Override
    public AuthenticationToken getAuthenticationToken() throws SmartlingApiException
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
                            logRefreshTokenDetails(refreshToken);
                            throw ex;
                        }
                    }
                    else
                    {
                        authenticationContext = authApiClient.authenticate(userId, userSecret).retrieveData();
                    }

                    LOGGER.trace("Got a new access token which will expire at " + authenticationContext.calculateAccessTokenExpireTime());
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

    private static void logRefreshTokenDetails(final String refreshToken)
    {
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
            LOGGER.warn("Failed to parse refresh token:", e);
        }
    }
}
