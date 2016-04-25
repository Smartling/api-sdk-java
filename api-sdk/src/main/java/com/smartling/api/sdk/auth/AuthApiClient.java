package com.smartling.api.sdk.auth;

import com.google.gson.reflect.TypeToken;
import com.smartling.api.sdk.BaseApiClient;
import com.smartling.api.sdk.ProxyConfiguration;
import com.smartling.api.sdk.dto.file.StringResponse;
import com.smartling.api.sdk.exceptions.SmartlingApiException;
import com.smartling.api.sdk.file.response.ApiV2ResponseWrapper;
import com.smartling.api.sdk.file.response.Response;
import org.apache.http.client.methods.HttpPost;

public class AuthApiClient extends BaseApiClient
{
    public static final String AUTH_API_V2_AUTHENTICATE = "/auth-api/v2/authenticate";
    public static final String AUTH_API_V2_REFRESH = "/auth-api/v2/authenticate/refresh";
    private final ProxyConfiguration proxyConfiguration;

    public AuthApiClient()
    {
        this(null, DEFAULT_BASE_URL);
    }

    public AuthApiClient(final ProxyConfiguration proxyConfiguration)
    {
        this(proxyConfiguration, DEFAULT_BASE_URL);
    }

    public AuthApiClient(final String baseUrl)
    {
        this(null, baseUrl);
    }

    public AuthApiClient(final ProxyConfiguration proxyConfiguration, final String baseUrl)
    {
        this.proxyConfiguration = proxyConfiguration;
        this.baseUrl = baseUrl;
    }

    public Response<AuthenticationContext> authenticate(AuthenticationCommand authenticationCommand)
            throws SmartlingApiException
    {
        final HttpPost httpPost = createJsonPostRequest(
                getApiUrl(AUTH_API_V2_AUTHENTICATE, baseUrl),
                authenticationCommand
        );

        final StringResponse response = httpUtils.executeHttpCall(httpPost, proxyConfiguration);

        return getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<AuthenticationContext>>() {});
    }

    public Response<AuthenticationContext> refresh(String refreshKey)
            throws SmartlingApiException
    {
        final HttpPost httpPost = createJsonPostRequest(
                getApiUrl(AUTH_API_V2_REFRESH, baseUrl),
                refreshKey
        );

        final StringResponse response = httpUtils.executeHttpCall(httpPost, proxyConfiguration);

        return getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<AuthenticationContext>>() {});
    }

    private String getApiUrl(final String url, String baseAuthApiUrl)
    {
        return baseAuthApiUrl + url;
    }

}
