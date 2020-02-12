package com.smartling.api.sdk.file;

import com.google.gson.reflect.TypeToken;
import com.smartling.api.sdk.HttpClientConfiguration;
import com.smartling.api.sdk.ProxyConfiguration;
import com.smartling.api.sdk.TokenProviderAwareClient;
import com.smartling.api.sdk.auth.AuthApiClient;
import com.smartling.api.sdk.auth.AuthenticationToken;
import com.smartling.api.sdk.auth.ExistingTokenProvider;
import com.smartling.api.sdk.auth.OAuthTokenProvider;
import com.smartling.api.sdk.auth.TokenProvider;
import com.smartling.api.sdk.dto.file.StringResponse;
import com.smartling.api.sdk.exceptions.SmartlingApiException;
import com.smartling.api.sdk.file.parameters.FileListSearchParameterBuilder;
import com.smartling.api.sdk.file.response.ApiV2ResponseWrapper;
import com.smartling.api.sdk.file.response.FileList;
import com.smartling.api.sdk.file.response.UidAwareFileListItem;
import com.smartling.api.sdk.util.DateFormatter;
import org.apache.commons.lang3.CharEncoding;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.smartling.api.sdk.file.parameters.FileApiParameter.FILE_TYPES;
import static com.smartling.api.sdk.file.parameters.FileApiParameter.LAST_UPLOADED_AFTER;
import static com.smartling.api.sdk.file.parameters.FileApiParameter.LAST_UPLOADED_BEFORE;
import static com.smartling.api.sdk.file.parameters.FileApiParameter.LIMIT;
import static com.smartling.api.sdk.file.parameters.FileApiParameter.OFFSET;
import static com.smartling.api.sdk.file.parameters.FileApiParameter.URI_MASK;

public final class AccountBasedFileApiClientImpl extends TokenProviderAwareClient implements AccountBasedFileApiClient
{

    private static final String ACCOUNT_BASED_FILES_API_V2_FILES_LIST = "/files-api/v2/accounts/%s/files/list";

    private static final String REQUEST_PARAMS_SEPARATOR = "?";

    private final String accountUid;

    private AccountBasedFileApiClientImpl(final TokenProvider tokenProvider, final String accountUid, final ProxyConfiguration proxyConfiguration,
                                          final String baseUrl, final HttpClientConfiguration httpClientConfiguration)
    {
        super(baseUrl, proxyConfiguration, tokenProvider, httpClientConfiguration);
        this.accountUid = Objects.requireNonNull(accountUid, "Account UID can not be null");
    }

    @Override
    public FileList<UidAwareFileListItem> getFilesList(FileListSearchParameterBuilder fileListSearchParameterBuilder) throws SmartlingApiException
    {
        final String params = buildFileListParams(fileListSearchParameterBuilder);
        final HttpGet httpGet = new HttpGet(buildUrl(getApiUrl(ACCOUNT_BASED_FILES_API_V2_FILES_LIST), params));

        final StringResponse response = executeRequest(httpGet);

        return getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<FileList<UidAwareFileListItem>>>()
                {
                }
        ).retrieveData();
    }

    private String buildParamsQuery(NameValuePair... nameValuePairs)
    {
        final List<NameValuePair> params = new ArrayList<>();

        for (final NameValuePair nameValuePair : nameValuePairs)
        {
            if (nameValuePair.getValue() != null)
                params.add(nameValuePair);
        }

        return URLEncodedUtils.format(params, CharEncoding.UTF_8);
    }

    private String buildUrl(String apiServerUrl, String apiParameters)
    {
        return apiServerUrl + REQUEST_PARAMS_SEPARATOR + apiParameters;
    }

    private String buildFileListParams(FileListSearchParameterBuilder fileListSearchParameterBuilder)
    {
        final List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair(URI_MASK, fileListSearchParameterBuilder.getUriMask()));
        nameValuePairs.add(new BasicNameValuePair(LAST_UPLOADED_AFTER, DateFormatter.format(fileListSearchParameterBuilder.getLastUploadedAfter())));
        nameValuePairs.add(new BasicNameValuePair(LAST_UPLOADED_BEFORE, DateFormatter.format(fileListSearchParameterBuilder.getLastUploadedBefore())));
        nameValuePairs.add(new BasicNameValuePair(OFFSET, null == fileListSearchParameterBuilder.getOffset() ? null : String.valueOf(fileListSearchParameterBuilder.getOffset())));
        nameValuePairs.add(new BasicNameValuePair(LIMIT, null == fileListSearchParameterBuilder.getLimit() ? null : String.valueOf(fileListSearchParameterBuilder.getLimit())));
        nameValuePairs.addAll(convertFileTypesParams(FILE_TYPES, fileListSearchParameterBuilder.getFileTypes()));
        return buildParamsQuery(nameValuePairs.toArray(new NameValuePair[nameValuePairs.size()]));
    }

    private List<NameValuePair> convertFileTypesParams(final String prefix, final List<FileType> values)
    {
        if (values == null || values.isEmpty())
        {
            return Collections.emptyList();
        }

        final List<NameValuePair> nameValuePairs = new ArrayList<>();
        for (FileType value : values)
        {
            nameValuePairs.add(new BasicNameValuePair(prefix + "[]", value.getIdentifier()));
        }

        return nameValuePairs;
    }

    private String getApiUrl(String uri)
    {
        return baseUrl + String.format(uri, accountUid);
    }


    public static class Builder
    {
        private final String accountUid;

        private String baseSmartlingApiUrl = DEFAULT_BASE_URL;
        private TokenProvider tokenProvider;
        private String userId;
        private String userSecret;
        private ProxyConfiguration proxyConfiguration;
        private HttpClientConfiguration httpClientConfiguration;

        public Builder(String accountUid)
        {
            this.accountUid = accountUid;
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

        public Builder httpClientConfiguration(HttpClientConfiguration httpClientConfiguration)
        {
            this.httpClientConfiguration = httpClientConfiguration;
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

        public AccountBasedFileApiClient build()
        {
            TokenProvider tokenProvider = this.tokenProvider;
            if (tokenProvider == null && userId != null && userSecret != null) {
                tokenProvider = new OAuthTokenProvider(userId, userSecret, new AuthApiClient(proxyConfiguration, httpClientConfiguration, baseSmartlingApiUrl));
            }

            return new AccountBasedFileApiClientImpl(tokenProvider, accountUid, proxyConfiguration, baseSmartlingApiUrl, httpClientConfiguration);
        }
    }
}
