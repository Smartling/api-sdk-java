package com.smartling.api.sdk.projects;

import com.google.gson.reflect.TypeToken;
import com.smartling.api.sdk.ProxyConfiguration;
import com.smartling.api.sdk.TokenProviderAwareClient;
import com.smartling.api.sdk.auth.*;
import com.smartling.api.sdk.dto.file.StringResponse;
import com.smartling.api.sdk.exceptions.SmartlingApiException;
import com.smartling.api.sdk.file.response.ApiV2ResponseWrapper;
import com.smartling.api.sdk.file.response.EmptyResponse;
import com.smartling.api.sdk.projects.response.ProjectDetails;
import org.apache.http.client.methods.HttpGet;

import java.util.Objects;

public final class ProjectsApiClientImpl extends TokenProviderAwareClient implements ProjectsApiClient
{
    private static final String PROJECT_API_V2_DETAILS = "/projects-api/v2/projects/%s";

    private final String projectId;

    private ProjectsApiClientImpl(final TokenProvider tokenProvider, final String projectId, final ProxyConfiguration proxyConfiguration, final String baseUrl)
    {
        super(baseUrl, proxyConfiguration, tokenProvider);
        this.projectId = Objects.requireNonNull(projectId, "Project ID can not be null");
    }

    @Override
    public ProjectDetails getProjectDetails() throws SmartlingApiException
    {
        final HttpGet httpGet = new HttpGet(getApiUrl(PROJECT_API_V2_DETAILS));

        final StringResponse response = executeRequest(httpGet);

        if (response.isSuccess())
        {
            return getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<ProjectDetails>>()
                {
                }
            ).retrieveData();
        }
        else
        {
            // Trying to get Smartling API exception from a json response
            getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<EmptyResponse>>()
                    {
                    }
            ).retrieveData();
            // Throw exception if no Exception has been thrown in previously
            throw new SmartlingApiException("Failed to get projects details");
        }
    }

    private String getApiUrl(String uri)
    {
        return baseUrl + String.format(uri, projectId);
    }

    public static class Builder
    {
        private final String projectId;

        private String baseSmartlingApiUrl = DEFAULT_BASE_URL;
        private TokenProvider tokenProvider;
        private String userId;
        private String userSecret;
        private ProxyConfiguration proxyConfiguration;

        public Builder(String projectId)
        {
            this.projectId = projectId;
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
            this.userId = userId;
            this.userSecret = userSecret;
            this.tokenProvider = null;
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

        public ProjectsApiClientImpl build()
        {
            TokenProvider tokenProvider = this.tokenProvider;
            if (tokenProvider == null && userId != null && userSecret != null) {
                tokenProvider = new OAuthTokenProvider(userId, userSecret, new AuthApiClient(proxyConfiguration, baseSmartlingApiUrl));
            }

            return new ProjectsApiClientImpl(tokenProvider, projectId, proxyConfiguration, baseSmartlingApiUrl);
        }
    }
}
