package com.smartling.api.sdk.file;

import com.google.gson.reflect.TypeToken;
import com.smartling.api.sdk.BaseApiClient;
import com.smartling.api.sdk.ProxyConfiguration;
import com.smartling.api.sdk.auth.AuthApiClient;
import com.smartling.api.sdk.auth.AuthenticationToken;
import com.smartling.api.sdk.auth.ExistingTokenProvider;
import com.smartling.api.sdk.auth.OAuthTokenProvider;
import com.smartling.api.sdk.auth.TokenProvider;
import com.smartling.api.sdk.dto.file.FileLastModified;
import com.smartling.api.sdk.dto.file.StringResponse;
import com.smartling.api.sdk.dto.file.UploadFileData;
import com.smartling.api.sdk.exceptions.SmartlingApiException;
import com.smartling.api.sdk.file.parameters.AuthorizeLocalesPayload;
import com.smartling.api.sdk.file.parameters.FileApiParameter;
import com.smartling.api.sdk.file.parameters.FileDeletePayload;
import com.smartling.api.sdk.file.parameters.FileImportParameterBuilder;
import com.smartling.api.sdk.file.parameters.FileLastModifiedParameterBuilder;
import com.smartling.api.sdk.file.parameters.FileListSearchParameterBuilder;
import com.smartling.api.sdk.file.parameters.FileRenamePayload;
import com.smartling.api.sdk.file.parameters.FileUploadParameterBuilder;
import com.smartling.api.sdk.file.parameters.GetFileParameterBuilder;
import com.smartling.api.sdk.file.parameters.GetOriginalFileParameterBuilder;
import com.smartling.api.sdk.file.response.ApiV2ResponseWrapper;
import com.smartling.api.sdk.file.response.AuthorizedLocales;
import com.smartling.api.sdk.file.response.EmptyResponse;
import com.smartling.api.sdk.file.response.FileImportSmartlingData;
import com.smartling.api.sdk.file.response.FileList;
import com.smartling.api.sdk.file.response.FileLocaleStatus;
import com.smartling.api.sdk.file.response.FileStatus;
import com.smartling.api.sdk.util.DateFormatter;
import com.smartling.api.sdk.util.HttpUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpMessage;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.smartling.api.sdk.file.parameters.FileApiParameter.FILE_TYPES;
import static com.smartling.api.sdk.file.parameters.FileApiParameter.FILE_URI;
import static com.smartling.api.sdk.file.parameters.FileApiParameter.LAST_UPLOADED_AFTER;
import static com.smartling.api.sdk.file.parameters.FileApiParameter.LAST_UPLOADED_BEFORE;
import static com.smartling.api.sdk.file.parameters.FileApiParameter.LIMIT;
import static com.smartling.api.sdk.file.parameters.FileApiParameter.OFFSET;
import static com.smartling.api.sdk.file.parameters.FileApiParameter.URI_MASK;

public class FileApiClientImpl extends BaseApiClient implements FileApiClient
{
    public static final String FILES_API_V2_FILE_DELETE = "/files-api/v2/projects/%s/file/delete";
    public static final String FILES_API_V2_FILE_RENAME = "/files-api/v2/projects/%s/file/rename";
    public static final String FILES_API_V2_FILE_LAST_MODIFIED = "/files-api/v2/projects/%s/file/last_modified";
    public static final String FILES_API_V2_GET_FILE = "/files-api/v2/projects/%s/locales/%s/file";
    public static final String FILES_API_V2_GET_ORIGINAL_FILE = "/files-api/v2/projects/%s/file";
    public static final String FILES_API_V2_FILES_LIST = "/files-api/v2/projects/%s/files/list";
    public static final String FILES_API_V2_FILE_LOCALE_STATUS = "/files-api/v2/projects/%s/locales/%s/file/status";
    public static final String FILES_API_V2_FILE_STATUS = "/files-api/v2/projects/%s/file/status";
    public static final String FILES_API_V2_FILE_UPLOAD = "/files-api/v2/projects/%s/file";
    public static final String FILES_API_V2_FILE_IMPORT = "/files-api/v2/projects/%s/locales/%s/file/import";
    public static final String FILES_API_V2_AUTHORIZED_LOCALES = "/files-api/v2/projects/%s/file/authorized_locales";

    private static final String LOCALE_IDS_PARAM = "localeIds[]";

    private static final String REQUEST_PARAMS_SEPARATOR = "?";
    private static final String TEXT_PLAIN_TYPE = "text/plain";

    private TokenProvider tokenProvider;

    private String projectId;
    private ProxyConfiguration proxyConfiguration;
    private String baseSmartlingApiUrl = DEFAULT_API_GATEWAY_URL;

    private FileApiClientImpl(final TokenProvider tokenProvider, final String projectId, final ProxyConfiguration proxyConfiguration, final String baseSmartlingApiUrl)
    {
        this.httpUtils = new HttpUtils();
        this.tokenProvider = tokenProvider;
        this.projectId = projectId;
        this.proxyConfiguration = proxyConfiguration;
        this.baseSmartlingApiUrl = baseSmartlingApiUrl;
    }

    @Override public void setTokenProvider(final TokenProvider tokenProvider)
    {
        this.tokenProvider = tokenProvider;
    }

    @Override public UploadFileData uploadFile(File fileToUpload, FileUploadParameterBuilder fileUploadParameterBuilder) throws SmartlingApiException
    {
        FileBody fileBody = new FileBody(fileToUpload, createContentType(fileUploadParameterBuilder.getFileType(), getCharset(fileUploadParameterBuilder)), fileToUpload.getName());
        return uploadFile(fileUploadParameterBuilder, fileBody);
    }

    @Override public UploadFileData uploadFile(InputStream inputStream, String fileName, FileUploadParameterBuilder fileUploadParameterBuilder)
            throws SmartlingApiException
    {
        InputStreamBody inputStreamBody = new InputStreamBody(inputStream, createContentType(fileUploadParameterBuilder.getFileType(), getCharset(fileUploadParameterBuilder)), fileName);
        return uploadFile(fileUploadParameterBuilder, inputStreamBody);
    }

    @Override public EmptyResponse deleteFile(String fileUri) throws SmartlingApiException
    {
        final HttpPost httpPost = createJsonPostRequest(
                getApiUrl(FILES_API_V2_FILE_DELETE, baseSmartlingApiUrl, projectId),
                new FileDeletePayload(fileUri)
        );
        addAuthorizationHeader(httpPost);

        final StringResponse response = httpUtils.executeHttpCall(httpPost, proxyConfiguration);

        return getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<EmptyResponse>>()
                {
                }
        ).retrieveData();
    }

    @Override public EmptyResponse renameFile(String fileUri, String newFileUri) throws SmartlingApiException
    {
        final HttpPost httpPost = createJsonPostRequest(
                getApiUrl(FILES_API_V2_FILE_RENAME, baseSmartlingApiUrl, projectId),
                new FileRenamePayload(fileUri, newFileUri)
        );
        addAuthorizationHeader(httpPost);

        final StringResponse response = httpUtils.executeHttpCall(httpPost, proxyConfiguration);

        return getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<EmptyResponse>>()
                {
                }
        ).retrieveData();
    }

    @Override public FileLastModified getLastModified(FileLastModifiedParameterBuilder builder) throws SmartlingApiException
    {
        final HttpGet httpGet = new HttpGet(buildUrl(getApiUrl(FILES_API_V2_FILE_LAST_MODIFIED, baseSmartlingApiUrl, projectId), buildParamsQuery(
                        builder.getNameValueList().toArray(new NameValuePair[builder.getNameValueList().size()])
                )
        )
        );
        addAuthorizationHeader(httpGet);

        final StringResponse response = httpUtils.executeHttpCall(httpGet, proxyConfiguration);

        return getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<FileLastModified>>()
                {
                }
        ).retrieveData();
    }

    @Override public StringResponse getFile(GetFileParameterBuilder getFileParameterBuilder) throws SmartlingApiException
    {
        final List<NameValuePair> paramsList = getFileParameterBuilder.getNameValueList();
        final String params = buildParamsQuery(paramsList.toArray(new NameValuePair[paramsList.size()]));

        final HttpGet httpGet = new HttpGet(buildUrl(getApiUrl(FILES_API_V2_GET_FILE, getFileParameterBuilder.getLocale(), baseSmartlingApiUrl, projectId), params));
        addAuthorizationHeader(httpGet);

        return httpUtils.executeHttpCall(httpGet, proxyConfiguration);
    }

    @Override public StringResponse getOriginalFile(GetOriginalFileParameterBuilder getFileParameterBuilder) throws SmartlingApiException
    {
        final List<NameValuePair> paramsList = getFileParameterBuilder.getNameValueList();
        final String params = buildParamsQuery(paramsList.toArray(new NameValuePair[paramsList.size()]));

        final HttpGet httpGet = new HttpGet(buildUrl(getApiUrl(FILES_API_V2_GET_ORIGINAL_FILE, baseSmartlingApiUrl, projectId), params));
        addAuthorizationHeader(httpGet);

        return httpUtils.executeHttpCall(httpGet, proxyConfiguration);
    }

    @Override public FileList getFilesList(FileListSearchParameterBuilder fileListSearchParameterBuilder) throws SmartlingApiException
    {
        final String params = buildFileListParams(fileListSearchParameterBuilder);
        final HttpGet httpGet = new HttpGet(buildUrl(getApiUrl(FILES_API_V2_FILES_LIST, baseSmartlingApiUrl, projectId), params));
        addAuthorizationHeader(httpGet);

        final StringResponse response = httpUtils.executeHttpCall(httpGet, proxyConfiguration);

        return getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<FileList>>()
                {
                }
        ).retrieveData();
    }

    @Override public FileLocaleStatus getFileLocaleStatus(String fileUri, String locale) throws SmartlingApiException
    {
        final String params = buildParamsQuery(new BasicNameValuePair(FILE_URI, fileUri));
        final HttpGet httpGet = new HttpGet(buildUrl(getApiUrl(FILES_API_V2_FILE_LOCALE_STATUS, locale, baseSmartlingApiUrl, projectId), params));
        addAuthorizationHeader(httpGet);

        final StringResponse response = httpUtils.executeHttpCall(httpGet, proxyConfiguration);

        return getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<FileLocaleStatus>>()
                {
                }
        ).retrieveData();
    }

    @Override public FileStatus getFileStatus(String fileUri) throws SmartlingApiException
    {
        final String params = buildParamsQuery(new BasicNameValuePair(FILE_URI, fileUri));
        final HttpGet httpGet = new HttpGet(buildUrl(getApiUrl(FILES_API_V2_FILE_STATUS, baseSmartlingApiUrl, projectId), params));
        addAuthorizationHeader(httpGet);

        final StringResponse response = httpUtils.executeHttpCall(httpGet, proxyConfiguration);

        return getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<FileStatus>>()
                {
                }
        ).retrieveData();
    }

    @Override public FileImportSmartlingData importTranslations(FileImportParameterBuilder fileImportParameterBuilder)
            throws SmartlingApiException
    {
        FileBody fileBody = new FileBody(fileImportParameterBuilder.getFile(), createContentType(fileImportParameterBuilder.getFileType(), Charset.forName(fileImportParameterBuilder.getCharset())), fileImportParameterBuilder.getFile().getName());

        final List<NameValuePair> paramsList = fileImportParameterBuilder.getNameValueList();

        final MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create().addPart(FileApiParameter.FILE, fileBody);

        if (CollectionUtils.isNotEmpty(paramsList))
        {
            for (NameValuePair param : paramsList)
            {
                if (param.getValue() != null)
                {
                    multipartEntityBuilder.addPart(
                            param.getName(),
                            new StringBody(
                                    param.getValue(),
                                    ContentType.create(TEXT_PLAIN_TYPE, Charset.forName(CharEncoding.UTF_8))
                            )
                    );
                }
            }
        }

        final HttpPost httpPost = new HttpPost(baseSmartlingApiUrl + String.format(FILES_API_V2_FILE_IMPORT, projectId, fileImportParameterBuilder.getLocale()));
        httpPost.setEntity(multipartEntityBuilder.build());
        addAuthorizationHeader(httpPost);

        final StringResponse response = httpUtils.executeHttpCall(httpPost, proxyConfiguration);
        return getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<FileImportSmartlingData>>()
                {
                }
        ).retrieveData();
    }

    @Override public AuthorizedLocales getAuthorizedLocales(String fileUri) throws SmartlingApiException
    {
        final String params = buildParamsQuery(new BasicNameValuePair(FILE_URI, fileUri));
        final HttpGet httpGet = new HttpGet(buildUrl(getApiUrl(FILES_API_V2_AUTHORIZED_LOCALES, baseSmartlingApiUrl, projectId), params));
        addAuthorizationHeader(httpGet);

        final StringResponse response = httpUtils.executeHttpCall(httpGet, proxyConfiguration);

        return getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<AuthorizedLocales>>()
                {
                }
        ).retrieveData();
    }

    @Override public EmptyResponse authorizeLocales(String fileUri, String... localeIds) throws SmartlingApiException
    {
        final HttpPost httpPost = createJsonPostRequest(
                getApiUrl(FILES_API_V2_AUTHORIZED_LOCALES, baseSmartlingApiUrl, projectId),
                new AuthorizeLocalesPayload(fileUri, localeIds)
        );
        addAuthorizationHeader(httpPost);

        final StringResponse response = httpUtils.executeHttpCall(httpPost, proxyConfiguration);

        return getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<EmptyResponse>>()
                {
                }
        ).retrieveData();
    }

    @Override public EmptyResponse unAuthorizeLocales(String fileUri, String... localeIds) throws SmartlingApiException
    {
        final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair(FILE_URI, fileUri));
        if (localeIds != null)
        {
            for (String localeId : localeIds)
                nameValuePairs.add(new BasicNameValuePair(LOCALE_IDS_PARAM, localeId));
        }
        final String params = buildParamsQuery(nameValuePairs.toArray(new NameValuePair[0]));
        final HttpDelete httpDelete = new HttpDelete(buildUrl(getApiUrl(FILES_API_V2_AUTHORIZED_LOCALES, baseSmartlingApiUrl, projectId), params));
        addAuthorizationHeader(httpDelete);

        final StringResponse response = httpUtils.executeHttpCall(httpDelete, proxyConfiguration);

        return getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<EmptyResponse>>()
                {
                }
        ).retrieveData();
    }

    private Charset getCharset(final FileUploadParameterBuilder fileUploadParameterBuilder)
    {
        final String charsetName = fileUploadParameterBuilder.getCharset();
        return StringUtils.isEmpty(charsetName)?
                Charset.forName(charsetName) : null;
    }

    private ContentType createContentType(FileType fileType, Charset charset)
    {
        return fileType.isTextFormat() && charset != null
                ? ContentType.create(fileType.getMimeType(), charset)
                : ContentType.create(fileType.getMimeType());
    }

    private UploadFileData uploadFile(FileUploadParameterBuilder fileUploadParameterBuilder, ContentBody contentBody)
            throws SmartlingApiException
    {
        final List<NameValuePair> paramsList = fileUploadParameterBuilder.getNameValueList();

        final MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create()
                                                                                    .addPart(FileApiParameter.FILE, contentBody);

        if (CollectionUtils.isNotEmpty(paramsList))
        {
            for (NameValuePair param : paramsList)
            {
                if (param.getValue() != null)
                {
                    multipartEntityBuilder.addPart(
                            param.getName(),
                            new StringBody(
                                    param.getValue(),
                                    ContentType.create(TEXT_PLAIN_TYPE, Charset.forName(CharEncoding.UTF_8))
                            )
                    );
                }
            }
        }

        final HttpPost httpPost = new HttpPost(baseSmartlingApiUrl + String.format(FILES_API_V2_FILE_UPLOAD, projectId));
        httpPost.setEntity(multipartEntityBuilder.build());
        addAuthorizationHeader(httpPost);

        final StringResponse response = httpUtils.executeHttpCall(httpPost, proxyConfiguration);

        return getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<UploadFileData>>()
                {
                }
        ).retrieveData();
    }

    private void addAuthorizationHeader(final HttpMessage httpMessage) throws SmartlingApiException
    {
        httpMessage.addHeader(HttpHeaders.AUTHORIZATION, tokenProvider.getAuthenticationToken().getAuthorizationTokenString());
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
        final StringBuilder urlWithParameters = new StringBuilder(apiServerUrl);
        urlWithParameters.append(REQUEST_PARAMS_SEPARATOR);
        urlWithParameters.append(apiParameters);
        return urlWithParameters.toString();
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
            return Collections.emptyList();

        final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        for (int index = 0; index < values.size(); index++) {
            nameValuePairs.add(new BasicNameValuePair(prefix + "[]", values.get(index).getIdentifier()));
        }

        return nameValuePairs;
    }

    private List<BasicNameValuePair> getNameValuePairs(String name, List<String> values)
    {
        if (values == null || values.isEmpty())
            return Collections.emptyList();

        final List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
        for (final String value : values)
            nameValuePairs.add(new BasicNameValuePair(name, value));

        return nameValuePairs;
    }

    private String getApiUrl(String url, String baseFileApiUrl, String projectId)
    {
        return baseFileApiUrl + String.format(url, projectId);
    }

    private String getApiUrl(String url, String locale, String baseFileApiUrl, String projectId)
    {
        return baseFileApiUrl + String.format(url, projectId, locale);
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
            baseSmartlingApiUrl = "https://api.smartling.com";
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

        public FileApiClientImpl build() throws SmartlingApiException
        {
            sanityCheck();
            return new FileApiClientImpl(tokenProvider, projectId, proxyConfiguration, baseSmartlingApiUrl);
        }

        private void sanityCheck()
        {
            if (baseSmartlingApiUrl == null) throw new IllegalArgumentException("Wrong Configuration. baseSmartlingApiUrl should not be null");
            if (tokenProvider == null) throw new IllegalArgumentException("Wrong Configuration. tokenProvider should not be null");
        }
    }
}
