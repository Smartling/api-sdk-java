package com.smartling.api.sdk.file;

import com.google.gson.reflect.TypeToken;
import com.smartling.api.sdk.ProxyConfiguration;
import com.smartling.api.sdk.TokenProviderAwareClient;
import com.smartling.api.sdk.auth.AuthApiClient;
import com.smartling.api.sdk.auth.AuthenticationToken;
import com.smartling.api.sdk.auth.ExistingTokenProvider;
import com.smartling.api.sdk.auth.OAuthTokenProvider;
import com.smartling.api.sdk.auth.TokenProvider;
import com.smartling.api.sdk.dto.file.FileLastModified;
import com.smartling.api.sdk.dto.file.StringResponse;
import com.smartling.api.sdk.dto.file.UploadFileData;
import com.smartling.api.sdk.exceptions.SmartlingApiException;
import com.smartling.api.sdk.file.parameters.FileApiParameter;
import com.smartling.api.sdk.file.parameters.FileDeletePayload;
import com.smartling.api.sdk.file.parameters.FileImportParameterBuilder;
import com.smartling.api.sdk.file.parameters.FileLastModifiedParameterBuilder;
import com.smartling.api.sdk.file.parameters.FileListSearchParameterBuilder;
import com.smartling.api.sdk.file.parameters.FileRenamePayload;
import com.smartling.api.sdk.file.parameters.FileUploadParameterBuilder;
import com.smartling.api.sdk.file.parameters.GetFileParameterBuilder;
import com.smartling.api.sdk.file.parameters.GetFilesArchiveParameterBuilder;
import com.smartling.api.sdk.file.parameters.GetOriginalFileParameterBuilder;
import com.smartling.api.sdk.file.response.ApiV2ResponseWrapper;
import com.smartling.api.sdk.file.response.EmptyResponse;
import com.smartling.api.sdk.file.response.FileImportSmartlingData;
import com.smartling.api.sdk.file.response.FileList;
import com.smartling.api.sdk.file.response.FileLocaleStatus;
import com.smartling.api.sdk.file.response.FileStatus;
import com.smartling.api.sdk.util.DateFormatter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
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
import java.util.Objects;

import static com.smartling.api.sdk.file.parameters.FileApiParameter.FILE_TYPES;
import static com.smartling.api.sdk.file.parameters.FileApiParameter.FILE_URI;
import static com.smartling.api.sdk.file.parameters.FileApiParameter.LAST_UPLOADED_AFTER;
import static com.smartling.api.sdk.file.parameters.FileApiParameter.LAST_UPLOADED_BEFORE;
import static com.smartling.api.sdk.file.parameters.FileApiParameter.LIMIT;
import static com.smartling.api.sdk.file.parameters.FileApiParameter.OFFSET;
import static com.smartling.api.sdk.file.parameters.FileApiParameter.URI_MASK;

public final class FileApiClientImpl extends TokenProviderAwareClient implements FileApiClient
{
    private static final String FILES_API_V2_FILE_DELETE        = "/files-api/v2/projects/%s/file/delete";
    private static final String FILES_API_V2_FILE_RENAME        = "/files-api/v2/projects/%s/file/rename";
    private static final String FILES_API_V2_FILE_LAST_MODIFIED = "/files-api/v2/projects/%s/file/last-modified";
    private static final String FILES_API_V2_GET_FILE           = "/files-api/v2/projects/%s/locales/%s/file";
    private static final String FILES_API_V2_GET_FILES_ZIP      = "/files-api/v2/projects/%s/files/zip";
    private static final String FILES_API_V2_GET_ORIGINAL_FILE  = "/files-api/v2/projects/%s/file";
    private static final String FILES_API_V2_FILES_LIST         = "/files-api/v2/projects/%s/files/list";
    private static final String FILES_API_V2_FILE_LOCALE_STATUS = "/files-api/v2/projects/%s/locales/%s/file/status";
    private static final String FILES_API_V2_FILE_STATUS        = "/files-api/v2/projects/%s/file/status";
    private static final String FILES_API_V2_FILE_UPLOAD        = "/files-api/v2/projects/%s/file";
    private static final String FILES_API_V2_FILE_IMPORT        = "/files-api/v2/projects/%s/locales/%s/file/import";

    private static final String REQUEST_PARAMS_SEPARATOR = "?";
    private static final String TEXT_PLAIN_TYPE = "text/plain";

    private final String projectId;

    private FileApiClientImpl(final TokenProvider tokenProvider, final String projectId, final ProxyConfiguration proxyConfiguration, final String baseUrl)
    {
        super(baseUrl, proxyConfiguration, tokenProvider);
        this.projectId = Objects.requireNonNull(projectId, "Project ID can not be null");
    }

    @Override
    public UploadFileData uploadFile(File fileToUpload, FileUploadParameterBuilder fileUploadParameterBuilder) throws SmartlingApiException
    {
        FileBody fileBody = new FileBody(fileToUpload, createContentType(fileUploadParameterBuilder.getFileType(), getCharset(fileUploadParameterBuilder)), fileToUpload.getName());
        return uploadFile(fileUploadParameterBuilder, fileBody);
    }

    @Override
    public UploadFileData uploadFile(InputStream inputStream, String fileName, FileUploadParameterBuilder fileUploadParameterBuilder)
            throws SmartlingApiException
    {
        InputStreamBody inputStreamBody = new InputStreamBody(inputStream, createContentType(fileUploadParameterBuilder.getFileType(), getCharset(fileUploadParameterBuilder)), fileName);
        return uploadFile(fileUploadParameterBuilder, inputStreamBody);
    }

    @Override
    public EmptyResponse deleteFile(String fileUri) throws SmartlingApiException
    {
        final HttpPost httpPost = createJsonPostRequest(
                getApiUrl(FILES_API_V2_FILE_DELETE),
                new FileDeletePayload(fileUri)
        );
        final StringResponse response = executeRequest(httpPost);

        return getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<EmptyResponse>>()
                {
                }
        ).retrieveData();
    }

    @Override
    public EmptyResponse renameFile(String fileUri, String newFileUri) throws SmartlingApiException
    {
        final HttpPost httpPost = createJsonPostRequest(
                getApiUrl(FILES_API_V2_FILE_RENAME),
                new FileRenamePayload(fileUri, newFileUri)
        );
        final StringResponse response = executeRequest(httpPost);

        return getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<EmptyResponse>>()
                {
                }
        ).retrieveData();
    }

    @Override
    public FileLastModified getLastModified(FileLastModifiedParameterBuilder builder) throws SmartlingApiException
    {
        final HttpGet httpGet = new HttpGet(buildUrl(getApiUrl(FILES_API_V2_FILE_LAST_MODIFIED), buildParamsQuery(
                        builder.getNameValueList().toArray(new NameValuePair[builder.getNameValueList().size()])
                )
        )
        );
        final StringResponse response = executeRequest(httpGet);

        return getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<FileLastModified>>()
                {
                }
        ).retrieveData();
    }

    @Override
    public StringResponse getFilesArchive(GetFilesArchiveParameterBuilder builder) throws SmartlingApiException
    {
        final List<NameValuePair> paramsList = builder.getNameValueList();
        final String params = buildParamsQuery(paramsList.toArray(new NameValuePair[paramsList.size()]));

        final HttpGet httpGet = new HttpGet(buildUrl(getApiUrl(FILES_API_V2_GET_FILES_ZIP), params));

        final StringResponse response = executeRequest(httpGet);

        if (response.isSuccess())
        {
            // This is a string representing the ZIP file contents
            return response;
        }
        else
        {
            // Trying to get Smartling API exception from a json response
            getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<EmptyResponse>>()
                    {
                    }
            ).retrieveData();
            // Throw exception if no Exception has been thrown in previously
            throw new SmartlingApiException("Failed to get file content");
        }
    }

    @Override
    public StringResponse getFile(GetFileParameterBuilder getFileParameterBuilder) throws SmartlingApiException
    {
        final List<NameValuePair> paramsList = getFileParameterBuilder.getNameValueList();
        final String params = buildParamsQuery(paramsList.toArray(new NameValuePair[paramsList.size()]));

        final HttpGet httpGet = new HttpGet(buildUrl(getApiUrl(FILES_API_V2_GET_FILE, getFileParameterBuilder.getLocale()), params));

        final StringResponse response = executeRequest(httpGet);
        if (response.isSuccess())
        {
            return response;
        }
        else
        {
            // Trying to get Smartling API exception from a json response
            getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<EmptyResponse>>()
                    {
                    }
            ).retrieveData();
            // Throw exception if no Exception has been thrown in previously
            throw new SmartlingApiException("Failed to get file content");
        }
    }

    @Override
    public StringResponse getOriginalFile(GetOriginalFileParameterBuilder getFileParameterBuilder) throws SmartlingApiException
    {
        final List<NameValuePair> paramsList = getFileParameterBuilder.getNameValueList();
        final String params = buildParamsQuery(paramsList.toArray(new NameValuePair[paramsList.size()]));

        final HttpGet httpGet = new HttpGet(buildUrl(getApiUrl(FILES_API_V2_GET_ORIGINAL_FILE), params));

        final StringResponse response = executeRequest(httpGet);
        if (response.isSuccess())
        {
            return response;
        }
        else
        {
            // Trying to get Smartling API exception from a json response
            getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<EmptyResponse>>()
                    {
                    }
            ).retrieveData();
            // Throw exception if no Exception has been thrown in previously
            throw new SmartlingApiException("Failed to get file content");
        }

    }

    @Override
    public FileList getFilesList(FileListSearchParameterBuilder fileListSearchParameterBuilder) throws SmartlingApiException
    {
        final String params = buildFileListParams(fileListSearchParameterBuilder);
        final HttpGet httpGet = new HttpGet(buildUrl(getApiUrl(FILES_API_V2_FILES_LIST), params));

        final StringResponse response = executeRequest(httpGet);

        return getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<FileList>>()
                {
                }
        ).retrieveData();
    }

    @Override
    public FileLocaleStatus getFileLocaleStatus(String fileUri, String locale) throws SmartlingApiException
    {
        final String params = buildParamsQuery(new BasicNameValuePair(FILE_URI, fileUri));
        final HttpGet httpGet = new HttpGet(buildUrl(getApiUrl(FILES_API_V2_FILE_LOCALE_STATUS, locale), params));

        final StringResponse response = executeRequest(httpGet);

        return getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<FileLocaleStatus>>()
                {
                }
        ).retrieveData();
    }

    @Override
    public FileStatus getFileStatus(String fileUri) throws SmartlingApiException
    {
        final String params = buildParamsQuery(new BasicNameValuePair(FILE_URI, fileUri));
        final HttpGet httpGet = new HttpGet(buildUrl(getApiUrl(FILES_API_V2_FILE_STATUS), params));

        final StringResponse response = executeRequest(httpGet);

        return getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<FileStatus>>()
                {
                }
        ).retrieveData();
    }

    @Override
    public FileImportSmartlingData importTranslations(FileImportParameterBuilder fileImportParameterBuilder)
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

        final HttpPost httpPost = new HttpPost(baseUrl + String.format(FILES_API_V2_FILE_IMPORT, projectId, fileImportParameterBuilder.getLocale()));
        httpPost.setEntity(multipartEntityBuilder.build());

        final StringResponse response = executeRequest(httpPost);

        return getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<FileImportSmartlingData>>()
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

        final HttpPost httpPost = new HttpPost(baseUrl + String.format(FILES_API_V2_FILE_UPLOAD, projectId));
        httpPost.setEntity(multipartEntityBuilder.build());

        final StringResponse response = executeRequest(httpPost);

        return getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<UploadFileData>>()
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
        return baseUrl + String.format(uri, projectId);
    }

    private String getApiUrl(String uri, String locale)
    {
        return baseUrl + String.format(uri, projectId, locale);
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

        public FileApiClient build()
        {
            TokenProvider tokenProvider = this.tokenProvider;
            if (tokenProvider == null && userId != null && userSecret != null) {
                tokenProvider = new OAuthTokenProvider(userId, userSecret, new AuthApiClient(proxyConfiguration, baseSmartlingApiUrl));
            }

            return new FileApiClientImpl(tokenProvider, projectId, proxyConfiguration, baseSmartlingApiUrl);
        }
    }
}
