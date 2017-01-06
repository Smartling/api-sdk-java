package com.smartling.api.sdk.project;

import com.google.gson.reflect.TypeToken;
import com.smartling.api.sdk.ProxyConfiguration;
import com.smartling.api.sdk.TokenProviderAwareClient;
import com.smartling.api.sdk.auth.AuthApiClient;
import com.smartling.api.sdk.auth.AuthenticationToken;
import com.smartling.api.sdk.auth.ExistingTokenProvider;
import com.smartling.api.sdk.auth.OAuthTokenProvider;
import com.smartling.api.sdk.auth.TokenProvider;
import com.smartling.api.sdk.dto.file.StringResponse;
import com.smartling.api.sdk.exceptions.SmartlingApiException;
import com.smartling.api.sdk.file.response.ApiV2ResponseWrapper;
import com.smartling.api.sdk.project.response.ProjectDetails;
import org.apache.http.client.methods.HttpGet;

public class ProjectApiClientImpl extends TokenProviderAwareClient implements ProjectApiClient
{
    public static final String PROJECTS_API_V2_PROJECT_DETAILS = "/projects-api/v2/projects/%s";

    private String projectId;

    private ProjectApiClientImpl(final TokenProvider tokenProvider, final String projectId, final ProxyConfiguration proxyConfiguration, final String baseUrl)
    {
        this.tokenProvider = tokenProvider;
        this.projectId = projectId;
        this.proxyConfiguration = proxyConfiguration;
        this.baseUrl = baseUrl;
    }

    @Override public ProjectDetails getDetails() throws SmartlingApiException
    {
        final HttpGet httpGet = new HttpGet(this.baseUrl + String.format(PROJECTS_API_V2_PROJECT_DETAILS, this.projectId));
        final StringResponse response = executeRequest(httpGet);

        return getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<ProjectDetails>>()
        {
        }).retrieveData();
    }

    public static class Builder
    {
        private TokenProvider tokenProvider;

        private final String projectId;
        private ProxyConfiguration proxyConfiguration;
        private String baseSmartlingApiUrl;

        public Builder(String projectId)
        {
            this.projectId = projectId;
            baseSmartlingApiUrl = DEFAULT_BASE_URL;
            proxyConfiguration = null;
        }

        public Builder baseSmartlingApiUrl(String baseAuthApiUrl)
        {
            this.baseSmartlingApiUrl = baseAuthApiUrl;
            return this;
        }

        public Builder proxyConfiguration(ProxyConfiguration proxyConfiguration)
        {
            this.proxyConfiguration = proxyConfiguration;
            return this;
        }

        public Builder authWithUserIdAndSecret(String userId, String userSecret)
        {
            tokenProvider = new OAuthTokenProvider(userId, userSecret, new AuthApiClient(proxyConfiguration, baseSmartlingApiUrl));
            return this;
        }

        public Builder authWithExistingToken(AuthenticationToken authenticationToken)
        {
            tokenProvider = new ExistingTokenProvider(authenticationToken);
            return this;
        }

        public Builder withCustomTokenProvider(TokenProvider tokenProvider)
        {
            this.tokenProvider = tokenProvider;
            return this;
        }

        public ProjectApiClient build()
        {
            sanityCheck();
            return new ProjectApiClientImpl(tokenProvider, projectId, proxyConfiguration, baseSmartlingApiUrl);
        }

        private void sanityCheck()
        {
            if (baseSmartlingApiUrl == null) throw new IllegalArgumentException("Wrong Configuration. baseUrl should not be null");
            if (tokenProvider == null) throw new IllegalArgumentException("Wrong Configuration. tokenProvider should not be null");
        }
    }
}
