package com.smartling.api.sdk.file;

import com.google.gson.reflect.TypeToken;
import com.smartling.api.sdk.BaseApiClient;
import com.smartling.api.sdk.ConnectionConfig;
import com.smartling.api.sdk.auth.AuthenticationContext;
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
import com.smartling.api.sdk.file.response.Response;
import com.smartling.api.sdk.util.DateFormatter;
import com.smartling.api.sdk.util.HttpUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.CharEncoding;
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

public class FileApiClient extends BaseApiClient
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

    private HttpUtils httpUtils;

    public FileApiClient()
    {
        this.httpUtils = new HttpUtils();
    }

    public Response<UploadFileData> uploadFile(AuthenticationContext authenticationContext, File fileToUpload, String charsetName,
            FileUploadParameterBuilder fileUploadParameterBuilder, ConnectionConfig config)
            throws SmartlingApiException
    {
        FileBody fileBody = new FileBody(fileToUpload, createContentType(fileUploadParameterBuilder.getFileType(), Charset.forName(charsetName)), fileToUpload.getName());
        return uploadFile(authenticationContext, fileUploadParameterBuilder, fileBody, config);
    }

    public Response<UploadFileData> uploadFile(AuthenticationContext authenticationContext, InputStream inputStream, String fileName, String charsetName,
            FileUploadParameterBuilder fileUploadParameterBuilder, ConnectionConfig config) throws SmartlingApiException
    {
        InputStreamBody inputStreamBody = new InputStreamBody(inputStream, createContentType(fileUploadParameterBuilder.getFileType(), Charset.forName(charsetName)), fileName);
        return uploadFile(authenticationContext, fileUploadParameterBuilder, inputStreamBody, config);
    }

    public Response<EmptyResponse> deleteFile(AuthenticationContext authenticationContext, String fileUri, ConnectionConfig config) throws SmartlingApiException
    {
        final HttpPost httpPost = createJsonPostRequest(
                getApiUrl(FILES_API_V2_FILE_DELETE, config.getBaseFileApiUrl(), config.getProjectId()),
                new FileDeletePayload(fileUri)
        );
        authenticationContext.applyTo(httpPost);

        final StringResponse response = httpUtils.executeHttpCall(httpPost, config.getProxyConfiguration());

        return getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<EmptyResponse>>()
                {
                }
        );
    }

    public Response<EmptyResponse> renameFile(AuthenticationContext authenticationContext, String fileUri, String newFileUri,
            ConnectionConfig config) throws SmartlingApiException
    {
        final HttpPost httpPost = createJsonPostRequest(
                getApiUrl(FILES_API_V2_FILE_RENAME, config.getBaseFileApiUrl(), config.getProjectId()),
                new FileRenamePayload(fileUri, newFileUri)
        );
        authenticationContext.applyTo(httpPost);

        final StringResponse response = httpUtils.executeHttpCall(httpPost, config.getProxyConfiguration());

        return getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<EmptyResponse>>()
                {
                }
        );
    }

    public Response<FileLastModified> getLastModified(AuthenticationContext authenticationContext, FileLastModifiedParameterBuilder builder, ConnectionConfig config) throws
                                                                                                                                                                      SmartlingApiException
    {
        final HttpGet httpGet = new HttpGet(buildUrl(getApiUrl(FILES_API_V2_FILE_LAST_MODIFIED, config.getBaseFileApiUrl(), config.getProjectId()), buildParamsQuery(
                        builder.getNameValueList().toArray(new NameValuePair[builder.getNameValueList().size()])
                )
        )
        );
        authenticationContext.applyTo(httpGet);

        final StringResponse response = httpUtils.executeHttpCall(httpGet, config.getProxyConfiguration());

        return getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<FileLastModified>>()
                {
                }
        );
    }

    public StringResponse getFile(AuthenticationContext authenticationContext, String locale, GetFileParameterBuilder getFileParameterBuilder,
            ConnectionConfig config) throws SmartlingApiException
    {
        final List<NameValuePair> paramsList = getFileParameterBuilder.getNameValueList();
        final String params = buildParamsQuery(paramsList.toArray(new NameValuePair[paramsList.size()]));

        final HttpGet httpGet = new HttpGet(buildUrl(getApiUrl(FILES_API_V2_GET_FILE, locale, config.getBaseFileApiUrl(), config.getProjectId()), params));
        authenticationContext.applyTo(httpGet);

        return httpUtils.executeHttpCall(httpGet, config.getProxyConfiguration());
    }

    public StringResponse getOriginalFile(AuthenticationContext authenticationContext, GetOriginalFileParameterBuilder getFileParameterBuilder,
            ConnectionConfig config) throws SmartlingApiException
    {
        final List<NameValuePair> paramsList = getFileParameterBuilder.getNameValueList();
        final String params = buildParamsQuery(paramsList.toArray(new NameValuePair[paramsList.size()]));

        final HttpGet httpGet = new HttpGet(buildUrl(getApiUrl(FILES_API_V2_GET_ORIGINAL_FILE, config.getBaseFileApiUrl(), config.getProjectId()), params));
        authenticationContext.applyTo(httpGet);

        return httpUtils.executeHttpCall(httpGet, config.getProxyConfiguration());
    }

    public Response<FileList> getFilesList(AuthenticationContext authenticationContext, FileListSearchParameterBuilder fileListSearchParameterBuilder,
            ConnectionConfig config) throws SmartlingApiException
    {
        final String params = buildFileListParams(fileListSearchParameterBuilder);
        final HttpGet httpGet = new HttpGet(buildUrl(getApiUrl(FILES_API_V2_FILES_LIST, config.getBaseFileApiUrl(), config.getProjectId()), params));
        authenticationContext.applyTo(httpGet);

        final StringResponse response = httpUtils.executeHttpCall(httpGet, config.getProxyConfiguration());

        return getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<FileList>>()
                {
                }
        );
    }

    public Response<FileLocaleStatus> getFileLocaleStatus(AuthenticationContext authenticationContext, String fileUri, String locale,
            ConnectionConfig config) throws SmartlingApiException
    {
        final String params = buildParamsQuery(new BasicNameValuePair(FILE_URI, fileUri));
        final HttpGet httpGet = new HttpGet(buildUrl(getApiUrl(FILES_API_V2_FILE_LOCALE_STATUS, locale, config.getBaseFileApiUrl(), config.getProjectId()), params));
        authenticationContext.applyTo(httpGet);

        final StringResponse response = httpUtils.executeHttpCall(httpGet, config.getProxyConfiguration());

        return getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<FileLocaleStatus>>()
                {
                }
        );
    }

    public Response<FileStatus> getFileStatus(AuthenticationContext authenticationContext, String fileUri, ConnectionConfig config) throws SmartlingApiException
    {
        final String params = buildParamsQuery(new BasicNameValuePair(FILE_URI, fileUri));
        final HttpGet httpGet = new HttpGet(buildUrl(getApiUrl(FILES_API_V2_FILE_STATUS, config.getBaseFileApiUrl(), config.getProjectId()), params));
        authenticationContext.applyTo(httpGet);

        final StringResponse response = httpUtils.executeHttpCall(httpGet, config.getProxyConfiguration());

        return getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<FileStatus>>()
                {
                }
        );
    }

    public Response<FileImportSmartlingData> importTranslations(AuthenticationContext authenticationContext, File fileToUpload, String locale, String charsetName,
            FileImportParameterBuilder fileImportParameterBuilder, ConnectionConfig config)
            throws SmartlingApiException
    {
        FileBody fileBody = new FileBody(fileToUpload, createContentType(fileImportParameterBuilder.getFileType(), Charset.forName(charsetName)), fileToUpload.getName());

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

        final HttpPost httpPost = new HttpPost(config.getBaseFileApiUrl() + String.format(FILES_API_V2_FILE_IMPORT, config.getProjectId(), locale));
        httpPost.setEntity(multipartEntityBuilder.build());
        authenticationContext.applyTo(httpPost);

        final StringResponse response = httpUtils.executeHttpCall(httpPost, config.getProxyConfiguration());
        return getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<FileImportSmartlingData>>()
                {
                }
        );
    }

    public Response<AuthorizedLocales> getAuthorizedLocales(AuthenticationContext authenticationContext, String fileUri, ConnectionConfig config) throws SmartlingApiException
    {
        final String params = buildParamsQuery(new BasicNameValuePair(FILE_URI, fileUri));
        final HttpGet httpGet = new HttpGet(buildUrl(getApiUrl(FILES_API_V2_AUTHORIZED_LOCALES, config.getBaseFileApiUrl(), config.getProjectId()), params));
        authenticationContext.applyTo(httpGet);

        final StringResponse response = httpUtils.executeHttpCall(httpGet, config.getProxyConfiguration());

        return getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<AuthorizedLocales>>()
                {
                }
        );
    }

    public Response<EmptyResponse> authorizeLocales(AuthenticationContext authenticationContext, String fileUri, String[] localeIds,
            ConnectionConfig config) throws SmartlingApiException
    {
        final HttpPost httpPost = createJsonPostRequest(
                getApiUrl(FILES_API_V2_AUTHORIZED_LOCALES, config.getBaseFileApiUrl(), config.getProjectId()),
                new AuthorizeLocalesPayload(fileUri, localeIds)
        );
        authenticationContext.applyTo(httpPost);

        final StringResponse response = httpUtils.executeHttpCall(httpPost, config.getProxyConfiguration());

        return getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<EmptyResponse>>()
                {
                }
        );
    }

    public Response<EmptyResponse> unAuthorizeLocales(AuthenticationContext authenticationContext, String fileUri, String[] localeIds,
            ConnectionConfig config) throws SmartlingApiException
    {
        final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair(FILE_URI, fileUri));
        if (localeIds != null)
        {
            for (String localeId : localeIds)
                nameValuePairs.add(new BasicNameValuePair(LOCALE_IDS_PARAM, localeId));
        }
        final String params = buildParamsQuery(nameValuePairs.toArray(new NameValuePair[0]));
        final HttpDelete httpDelete = new HttpDelete(buildUrl(getApiUrl(FILES_API_V2_AUTHORIZED_LOCALES, config.getBaseFileApiUrl(), config.getProjectId()), params));
        authenticationContext.applyTo(httpDelete);

        final StringResponse response = httpUtils.executeHttpCall(httpDelete, config.getProxyConfiguration());

        return getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<EmptyResponse>>()
                {
                }
        );
    }

    private ContentType createContentType(FileType fileType, Charset charset)
    {
        return fileType.isTextFormat()
                ? ContentType.create(fileType.getMimeType(), charset)
                : ContentType.create(fileType.getMimeType());
    }

    private Response<UploadFileData> uploadFile(AuthenticationContext authenticationContext, FileUploadParameterBuilder fileUploadParameterBuilder,
            ContentBody contentBody, ConnectionConfig config)
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

        final HttpPost httpPost = new HttpPost(config.getBaseFileApiUrl() + String.format(FILES_API_V2_FILE_UPLOAD, config.getProjectId()));
        httpPost.setEntity(multipartEntityBuilder.build());
        authenticationContext.applyTo(httpPost);

        final StringResponse response = httpUtils.executeHttpCall(httpPost, config.getProxyConfiguration());

        return getApiV2Response(response.getContents(), new TypeToken<ApiV2ResponseWrapper<UploadFileData>>()
                {
                }
        );
    }

    protected String buildParamsQuery(NameValuePair... nameValuePairs)
    {
        final List<NameValuePair> params = new ArrayList<>();

        for (final NameValuePair nameValuePair : nameValuePairs)
        {
            if (nameValuePair.getValue() != null)
                params.add(nameValuePair);
        }

        return URLEncodedUtils.format(params, CharEncoding.UTF_8);
    }

    protected String buildUrl(String apiServerUrl, String apiParameters)
    {
        final StringBuilder urlWithParameters = new StringBuilder(apiServerUrl);
        urlWithParameters.append(REQUEST_PARAMS_SEPARATOR);
        urlWithParameters.append(apiParameters);
        return urlWithParameters.toString();
    }

    private String buildFileListParams(FileListSearchParameterBuilder fileListSearchParameterBuilder)
    {
        final List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
        nameValuePairs.add(new BasicNameValuePair(URI_MASK, fileListSearchParameterBuilder.getUriMask()));
        nameValuePairs.add(new BasicNameValuePair(LAST_UPLOADED_AFTER, DateFormatter.format(fileListSearchParameterBuilder.getLastUploadedAfter())));
        nameValuePairs.add(new BasicNameValuePair(LAST_UPLOADED_BEFORE, DateFormatter.format(fileListSearchParameterBuilder.getLastUploadedBefore())));
        nameValuePairs.add(new BasicNameValuePair(OFFSET, null == fileListSearchParameterBuilder.getOffset() ? null : String.valueOf(fileListSearchParameterBuilder.getOffset())));
        nameValuePairs.add(new BasicNameValuePair(LIMIT, null == fileListSearchParameterBuilder.getLimit() ? null : String.valueOf(fileListSearchParameterBuilder.getLimit())));
        nameValuePairs.addAll(getNameValuePairs(FILE_TYPES, fileListSearchParameterBuilder.getFileTypes()));

        return buildParamsQuery(nameValuePairs.toArray(new NameValuePair[nameValuePairs.size()]));
    }

    protected List<BasicNameValuePair> getNameValuePairs(String name, List<String> values)
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

    public void setHttpUtils(HttpUtils httpUtils)
    {
        this.httpUtils = httpUtils;
    }
}
