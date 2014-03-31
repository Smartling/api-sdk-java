/*
 * Copyright 2012 Smartling, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this work except in compliance with the License.
 * You may obtain a copy of the License in the LICENSE file, or at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.smartling.api.sdk.file;

import static com.smartling.api.sdk.file.FileApiParams.API_KEY;
import static com.smartling.api.sdk.file.FileApiParams.CONDITIONS;
import static com.smartling.api.sdk.file.FileApiParams.FILE_TYPES;
import static com.smartling.api.sdk.file.FileApiParams.FILE_URI;
import static com.smartling.api.sdk.file.FileApiParams.LAST_MODIFIED_AFTER;
import static com.smartling.api.sdk.file.FileApiParams.LIMIT;
import static com.smartling.api.sdk.file.FileApiParams.LOCALE;
import static com.smartling.api.sdk.file.FileApiParams.OFFSET;
import static com.smartling.api.sdk.file.FileApiParams.ORDERBY;
import static com.smartling.api.sdk.file.FileApiParams.PROJECT_ID;
import static com.smartling.api.sdk.file.FileApiParams.LAST_UPLOADED_AFTER;
import static com.smartling.api.sdk.file.FileApiParams.LAST_UPLOADED_BEFORE;
import static com.smartling.api.sdk.file.FileApiParams.URI_MASK;
import static com.smartling.api.sdk.file.FileApiParams.RETRIEVAL_TYPE;
import static com.smartling.api.sdk.file.FileApiParams.NEW_FILE_URI;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.smartling.api.sdk.file.parameters.FileUploadParameterBuilder;
import com.smartling.api.sdk.file.response.ApiResponse;
import com.smartling.api.sdk.file.response.ApiResponseWrapper;
import com.smartling.api.sdk.file.response.Data;
import com.smartling.api.sdk.file.response.EmptyResponse;
import com.smartling.api.sdk.file.response.FileLastModified;
import com.smartling.api.sdk.file.response.FileList;
import com.smartling.api.sdk.file.response.FileStatus;
import com.smartling.api.sdk.file.response.StringResponse;
import com.smartling.api.sdk.file.response.UploadData;
import com.smartling.api.sdk.file.util.DateFormatter;
import com.smartling.api.sdk.file.util.DateTypeAdapter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.*;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * Base implementation of the {@link FileApiClientAdapter}.
 */
public class FileApiClientAdapterImpl implements FileApiClientAdapter
{
    private static final Log logger = LogFactory.getLog(FileApiClientAdapterImpl.class);

    private static final String API_KEY_MASK       = "%s-XXXXXXXXXXXX";
    private static final String RESPONSE_MESSAGES  = "Messages: %s";
    private static final String SUCCESS_CODE       = "SUCCESS";
    private static final String GENERAL_ERROR_CODE = "GENERAL_ERROR";

    private static final String MIME_TYPE                 = "text/plain";
    private static final String SMARTLING_API_URL         = "https://api.smartling.com/v1";
    private static final String SMARTLING_SANDBOX_API_URL = "https://sandbox-api.smartling.com/v1";
    private static final String UTF_16                    = "UTF-16";

    public static final String DEFAULT_ENCODING = "UTF-8";

    private static final String SCHEME_HTTPS                   = "https";
    private static final String SCHEME_HTTP                    = "http";
    private static final String PROPERTY_SUFFIX_PROXY_HOST     = ".proxyHost";
    private static final String PROPERTY_SUFFIX_PROXY_PORT     = ".proxyPort";
    private static final String PROPERTY_SUFFIX_PROXY_USERNAME = ".proxyUsername";
    private static final String PROPERTY_SUFFIX_PROXY_PASSWORD = ".proxyPassword";

    private final static String UPLOAD_FILE_API_URL     = "%s/file/upload?";
    private final static String GET_FILE_API_URL        = "%s/file/get?";
    private final static String GET_FILE_LIST_API_URL   = "%s/file/list?";
    private final static String GET_FILE_STATUS_API_URL = "%s/file/status?";
    private final static String GET_FILE_LAST_MODIFIED  = "%s/file/last_modified?";
    private final static String RENAME_FILE_URL         = "%s/file/rename?";
    private final static String DELETE_FILE_URL         = "%s/file/delete?";

    private String baseApiUrl;
    private String apiKey;
    private String projectId;

    private ProxyConfiguration proxyConfiguration;

    /**
     * Instantiate a {@link FileApiClientAdapterImpl} using the production mode setting (non sandbox).
     *
     * @param apiKey    your apiKey. Can be found at https://dashboard.smartling.com/settings/api
     * @param projectId your projectId. Can be found at https://dashboard.smartling.com/settings/api
     */
    public FileApiClientAdapterImpl(String apiKey, String projectId)
    {
        this(SMARTLING_API_URL, apiKey, projectId);
    }

    /**
     * Instantiate a {@link FileApiClientAdapterImpl} using the production mode setting (non sandbox).
     *
     * @param apiKey             your apiKey. Can be found at https://dashboard.smartling.com/settings/api
     * @param projectId          your projectId. Can be found at https://dashboard.smartling.com/settings/api
     * @param proxyConfiguration proxy configuration, pass {@code NULL} to never use proxy
     */
    public FileApiClientAdapterImpl(String apiKey, String projectId, ProxyConfiguration proxyConfiguration)
    {
        this(SMARTLING_API_URL, apiKey, projectId, proxyConfiguration);
    }

    /**
     * Instantiate a {@link FileApiClientAdapterImpl}.
     *
     * @param productionMode True if the production version of the api should be used, false if the Sandbox should be used.
     *                       It is recommended when first integrating your application with the API, that you use the Sandbox and not the production version.
     *                       For more information on the Sandbox, please see https://docs.smartling.com.
     * @param apiKey         your apiKey. Can be found at https://dashboard.smartling.com/settings/api
     * @param projectId      your projectId. Can be found at https://dashboard.smartling.com/settings/api
     */
    public FileApiClientAdapterImpl(boolean productionMode, String apiKey, String projectId)
    {
        this(productionMode ? SMARTLING_API_URL : SMARTLING_SANDBOX_API_URL, apiKey, projectId);
    }

    /**
     * Instantiate a {@link FileApiClientAdapterImpl}.
     *
     * @param productionMode     True if the production version of the api should be used, false if the Sandbox should be used.
     *                           It is recommended when first integrating your application with the API, that you use the Sandbox and not the production version.
     *                           For more information on the Sandbox, please see https://docs.smartling.com.
     * @param apiKey             your apiKey. Can be found at https://dashboard.smartling.com/settings/api
     * @param projectId          your projectId. Can be found at https://dashboard.smartling.com/settings/api
     * @param proxyConfiguration proxy configuration, pass {@code NULL} to never use proxy
     */
    public FileApiClientAdapterImpl(boolean productionMode, String apiKey, String projectId, ProxyConfiguration proxyConfiguration)
    {
        this(productionMode ? SMARTLING_API_URL : SMARTLING_SANDBOX_API_URL, apiKey, projectId, proxyConfiguration);
    }

    /**
     * Instantiate a {@link FileApiClientAdapterImpl}.
     *
     * @param baseApiUrl the apiUrl to use for interacting with the Smartling Translation API.
     * @param apiKey     your apiKey. Can be found at https://dashboard.smartling.com/settings/api
     * @param projectId  your projectId. Can be found at https://dashboard.smartling.com/settings/api
     */
    public FileApiClientAdapterImpl(String baseApiUrl, String apiKey, String projectId)
    {
        this(baseApiUrl, apiKey, projectId, null);
    }

    /**
     * Instantiate a {@link FileApiClientAdapterImpl}.
     *
     * @param baseApiUrl         the apiUrl to use for interacting with the Smartling Translation API.
     * @param apiKey             your apiKey. Can be found at https://dashboard.smartling.com/settings/api
     * @param projectId          your projectId. Can be found at https://dashboard.smartling.com/settings/api
     * @param proxyConfiguration proxy configuration, pass {@code NULL} to never use proxy
     */
    public FileApiClientAdapterImpl(String baseApiUrl, String apiKey, String projectId, ProxyConfiguration proxyConfiguration)
    {
        Assert.notNull(baseApiUrl, "Api url is required");
        Assert.notNull(apiKey, "apiKey is required");
        Assert.notNull(projectId, "projectId is required");

        this.baseApiUrl = baseApiUrl;
        this.apiKey = apiKey;
        this.projectId = projectId;
        this.proxyConfiguration = proxyConfiguration;
    }

    @Override
    public StringResponse getFile(String fileUri, String locale, RetrievalType retrievalType) throws FileApiException
    {
        logger.debug(String.format("Get file: fileUri = %s, projectId = %s, apiKey = %s, locale = %s",
                fileUri, this.projectId, maskApiKey(this.apiKey), locale));

        String params = buildParamsQuery(new BasicNameValuePair(FILE_URI, fileUri), new BasicNameValuePair(LOCALE, locale),
                new BasicNameValuePair(RETRIEVAL_TYPE, null == retrievalType ? null : retrievalType.name()));
        HttpGet getRequest = new HttpGet(buildUrl(GET_FILE_API_URL, params));
        try
        {
            StringResponse stringResponse = executeHttpCall(getRequest);
            logger.debug(String.format("Get file: %s", SUCCESS_CODE));

            return stringResponse;
        }
        catch (FileApiException fapiException)
        {
            logger.debug(String.format("Get file: %s. Exception message: %s", GENERAL_ERROR_CODE, fapiException.getMessage()));
            throw fapiException;
        }
    }

    @Override
    public ApiResponse<FileList> getFilesList(FileListSearchParams fileListSearchParams) throws FileApiException
    {
        logger.debug(String.format("Get files list: fileUriMask = %s, projectId = %s, apiKey = %s, locale = %s",
                fileListSearchParams.getUriMask(), this.projectId, maskApiKey(this.apiKey), fileListSearchParams.getLocale()));

        String params = buildFileListParams(fileListSearchParams);
        HttpGet getRequest = new HttpGet(buildUrl(GET_FILE_LIST_API_URL, params));

        try
        {
            StringResponse response = executeHttpCall(getRequest);
            ApiResponse apiResponse = getApiResponse(response.getContents(), new TypeToken<ApiResponseWrapper<FileList>>() {});
            logger.debug(String.format("Get files list: %s. %s", apiResponse.getCode(), getApiResponseMessages(apiResponse)));

            return apiResponse;
        }
        catch (FileApiException fapiException)
        {
            logger.debug(String.format("Get files list: %s. Exception message: %s", GENERAL_ERROR_CODE, fapiException.getMessage()));
            throw fapiException;
        }
    }

    @Override
    public ApiResponse<FileStatus> getFileStatus(String fileUri, String locale) throws FileApiException
    {
        logger.debug(String.format("Get file satatus: fileUri = %s, projectId = %s, apiKey = %s, locale = %s", fileUri, this.projectId, maskApiKey(this.apiKey), locale));

        String params = buildParamsQuery(new BasicNameValuePair(FILE_URI, fileUri), new BasicNameValuePair(LOCALE, locale));
        HttpGet getRequest = new HttpGet(buildUrl(GET_FILE_STATUS_API_URL, params));

        try
        {
            StringResponse response = executeHttpCall(getRequest);
            ApiResponse apiResponse = getApiResponse(response.getContents(), new TypeToken<ApiResponseWrapper<FileStatus>>() {});
            logger.debug(String.format("Get file status: %s. %s", apiResponse.getCode(), getApiResponseMessages(apiResponse)));

            return apiResponse;
        }
        catch (FileApiException fapiException)
        {
            logger.debug(String.format("Get file status: %s. Exception message: %s", GENERAL_ERROR_CODE, fapiException.getMessage()));
            throw fapiException;
        }
    }

    @Override
    public ApiResponse<UploadData> uploadFile(final File fileToUpload, final String fileEncoding,
                                              final FileUploadParameterBuilder fileUploadParameterBuilder)
            throws FileApiException
    {
        logger.debug(String.format("Upload file: fileUri = %s, projectId = %s, apiKey = %s, localesToApprove = %s",
                fileUploadParameterBuilder.getFileUri(), this.projectId, maskApiKey(this.apiKey), StringUtils.join(fileUploadParameterBuilder.getLocalesToApprove(), ", ")));

        final List<NameValuePair> paramsList = fileUploadParameterBuilder.getNameValueList();
        String params = buildParamsQuery(paramsList.toArray(new NameValuePair[paramsList.size()]));
        HttpPost httpPostFile = createFileUploadHttpPostRequest(params, fileToUpload, fileEncoding);

        try
        {
            StringResponse response = executeHttpCall(httpPostFile);
            ApiResponse apiResponse = getApiResponse(response.getContents(), new TypeToken<ApiResponseWrapper<UploadData>>() {});
            logger.debug(String.format("Upload file: %s. %s", apiResponse.getCode(), getApiResponseMessages(apiResponse)));

            return apiResponse;
        }
        catch (FileApiException fapiException)
        {
            logger.debug(String.format("Upload file: %s. Exception message: %s", GENERAL_ERROR_CODE, fapiException.getMessage()));
            throw fapiException;
        }
    }


    @Override
    public ApiResponse<EmptyResponse> deleteFile(String fileUri) throws FileApiException
    {
        logger.debug(String.format("Delete file: fileUri = %s, projectId = %s, apiKey = %s",
                fileUri, this.projectId, maskApiKey(this.apiKey)));

        String params = buildParamsQuery(new BasicNameValuePair(FILE_URI, fileUri));
        HttpDelete httpDeleteFileRequest = new HttpDelete(buildUrl(DELETE_FILE_URL, params));

        try
        {
            StringResponse response = executeHttpCall(httpDeleteFileRequest);
            ApiResponse apiResponse = getApiResponse(response.getContents(), new TypeToken<ApiResponseWrapper<EmptyResponse>>() {});
            logger.debug(String.format("Delete file: %s. %s", apiResponse.getCode(), getApiResponseMessages(apiResponse)));

            return apiResponse;
        }
        catch (FileApiException fapiException)
        {
            logger.debug(String.format("Delete file: %s. Exception message: %s", GENERAL_ERROR_CODE, fapiException.getMessage()));
            throw fapiException;
        }
    }

    @Override
    public ApiResponse<EmptyResponse> renameFile(String fileUri, String newFileUri) throws FileApiException
    {
        logger.debug(String.format("Rename file: fileUri = %s, projectId = %s, apiKey = %s",
                fileUri, this.projectId, maskApiKey(this.apiKey)));

        String params = buildParamsQuery(new BasicNameValuePair(FILE_URI, fileUri), new BasicNameValuePair(NEW_FILE_URI, newFileUri));
        HttpPost httpPostRequest = new HttpPost(buildUrl(RENAME_FILE_URL, params));

        try
        {
            StringResponse response = executeHttpCall(httpPostRequest);
            ApiResponse apiResponse = getApiResponse(response.getContents(), new TypeToken<ApiResponseWrapper<EmptyResponse>>() {});
            logger.debug(String.format("Rename file: %s. %s", apiResponse.getCode(), getApiResponseMessages(apiResponse)));

            return apiResponse;
        }
        catch (FileApiException fapiException)
        {
            logger.debug(String.format("Rename file: %s. Exception message: %s", GENERAL_ERROR_CODE, fapiException.getMessage()));
            throw fapiException;
        }
    }

    @Override
    public ApiResponse<FileLastModified> getLastModified(String fileUri, Date lastModifiedAfter, String locale) throws FileApiException
    {
        logger.debug(String.format("Get last modified: fileUri = %s, projectId = %s, apiKey = %s, locale = %s",
                fileUri, this.projectId, maskApiKey(this.apiKey), locale));

        String params = buildParamsQuery(
                new BasicNameValuePair(FILE_URI, fileUri),
                new BasicNameValuePair(LAST_MODIFIED_AFTER, DateFormatter.format(lastModifiedAfter)),
                new BasicNameValuePair(LOCALE, locale)
        );
        HttpGet getRequest = new HttpGet(buildUrl(GET_FILE_LAST_MODIFIED, params));

        try
        {
            StringResponse response = executeHttpCall(getRequest);
            ApiResponse apiResponse = getApiResponse(response.getContents(), new TypeToken<ApiResponseWrapper<FileLastModified>>() {});
            logger.debug(String.format("Get last modified: %s. %s", apiResponse.getCode(), getApiResponseMessages(apiResponse)));

            return apiResponse;
        }
        catch (FileApiException fapiException)
        {
            logger.debug(String.format("Get last modified: %s. Exception message: %s", GENERAL_ERROR_CODE, fapiException.getMessage()));
            throw fapiException;
        }
    }

    private HttpPost createFileUploadHttpPostRequest(String apiParameters, File fileToUpload, String fileEncoding)
    {
        MultipartEntity mpEntity = new MultipartEntity();
        ContentBody cbFile = new FileBody(fileToUpload, MIME_TYPE, fileEncoding);
        mpEntity.addPart(FileApiParams.FILE, cbFile);

        HttpPost httpPost = new HttpPost(String.format(UPLOAD_FILE_API_URL, baseApiUrl) + apiParameters);
        httpPost.setEntity(mpEntity);

        return httpPost;
    }

    private String buildUrl(String apiServerUrl, String apiParameters)
    {
        StringBuilder urlWithParameters = new StringBuilder(String.format(apiServerUrl, baseApiUrl));
        urlWithParameters.append(apiParameters);
        return urlWithParameters.toString();
    }

    private StringResponse executeHttpCall(HttpRequestBase httpRequest) throws FileApiException
    {
        HttpClient httpClient = null;
        try
        {
            httpClient = new DefaultHttpClient();
            setupProxy(httpClient);
            HttpResponse response = httpClient.execute(httpRequest);

            if (response.getStatusLine().getStatusCode() == HttpServletResponse.SC_OK)
                return inputStreamToString(response.getEntity().getContent(), null);

            throw new FileApiException(inputStreamToString(response.getEntity().getContent(), null).getContents());
        }
        catch (IOException e)
        {
            throw new FileApiException(e);
        }
        finally
        {
            if (null != httpClient)
                httpClient.getConnectionManager().shutdown();
        }
    }

    private void setupProxy(HttpClient httpClient)
    {
        String proxyHost = null;
        Integer proxyPort = null;
        String proxyUsername = null;
        String proxyPassword = null;

        if (proxyConfiguration != null)
        {
            proxyHost = proxyConfiguration.getHost();
            proxyPort = proxyConfiguration.getPort();
            proxyUsername = proxyConfiguration.getUsername();
            proxyPassword = proxyConfiguration.getPassword();
        }
        else
        {
            String protocol = null;
            if (StringUtils.isNotBlank(System.getProperty(SCHEME_HTTPS + PROPERTY_SUFFIX_PROXY_HOST)) && StringUtils
                    .isNotBlank(System.getProperty(SCHEME_HTTPS + PROPERTY_SUFFIX_PROXY_PORT)))
                protocol = SCHEME_HTTPS;
            else if (StringUtils.isNotBlank(System.getProperty(SCHEME_HTTP + PROPERTY_SUFFIX_PROXY_HOST)) && StringUtils
                    .isNotBlank(System.getProperty(SCHEME_HTTP + PROPERTY_SUFFIX_PROXY_PORT)))
                protocol = SCHEME_HTTP;

            if (protocol != null)
            {
                proxyHost = System.getProperty(protocol + PROPERTY_SUFFIX_PROXY_HOST);
                proxyPort = Integer.valueOf(System.getProperty(protocol + PROPERTY_SUFFIX_PROXY_PORT));
                proxyUsername = System.getProperty(protocol + PROPERTY_SUFFIX_PROXY_USERNAME);
                proxyPassword = System.getProperty(protocol + PROPERTY_SUFFIX_PROXY_PASSWORD);
            }
        }

        if (proxyHost != null && proxyPort != null)
        {
            if (StringUtils.isNotBlank(proxyUsername) && StringUtils.isNotBlank(proxyPassword) && httpClient instanceof DefaultHttpClient)
            {
                ((DefaultHttpClient)httpClient).getCredentialsProvider().setCredentials(
                        new AuthScope(proxyHost, proxyPort),
                        new UsernamePasswordCredentials(proxyUsername, proxyPassword)
                );
            }

            HttpHost proxy = new HttpHost(proxyHost, proxyPort);
            httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        }
    }

    private StringResponse inputStreamToString(InputStream inputStream, String encoding) throws FileApiException
    {
        StringWriter writer = new StringWriter();
        try
        {
            // unless UTF-16 explicitly specified, use default UTF-8 encoding.
            String responseEncoding = (null == encoding || !encoding.toUpperCase().contains(UTF_16) ? DEFAULT_ENCODING : UTF_16);
            IOUtils.copy(inputStream, writer, responseEncoding);
            return new StringResponse(writer.toString(), responseEncoding);
        }
        catch (IOException e)
        {
            throw new FileApiException(e);
        }
    }

    private String buildFileListParams(FileListSearchParams fileListSearchParams)
    {
        List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
        nameValuePairs.add(new BasicNameValuePair(LOCALE, fileListSearchParams.getLocale()));
        nameValuePairs.add(new BasicNameValuePair(URI_MASK, fileListSearchParams.getUriMask()));
        nameValuePairs.add(new BasicNameValuePair(LAST_UPLOADED_AFTER, DateFormatter.format(fileListSearchParams.getLastUploadedAfter())));
        nameValuePairs.add(new BasicNameValuePair(LAST_UPLOADED_BEFORE, DateFormatter.format(fileListSearchParams.getLastUploadedBefore())));
        nameValuePairs.add(new BasicNameValuePair(OFFSET, null == fileListSearchParams.getOffset() ? null : String.valueOf(fileListSearchParams.getOffset())));
        nameValuePairs.add(new BasicNameValuePair(LIMIT, null == fileListSearchParams.getLimit() ? null : String.valueOf(fileListSearchParams.getLimit())));
        nameValuePairs.addAll(getNameValuePairs(FILE_TYPES, fileListSearchParams.getFileTypes()));
        nameValuePairs.addAll(getNameValuePairs(CONDITIONS, fileListSearchParams.getConditions()));
        nameValuePairs.addAll(getNameValuePairs(ORDERBY, fileListSearchParams.getOrderBy()));

        return buildParamsQuery(nameValuePairs.toArray(new NameValuePair[nameValuePairs.size()]));
    }

    private List<BasicNameValuePair> getNameValuePairs(String name, List<String> values)
    {
        if (CollectionUtils.isEmpty(values))
            return Collections.emptyList();

        List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
        for (String value : values)
            nameValuePairs.add(new BasicNameValuePair(name, value));

        return nameValuePairs;
    }

    private <T extends Data> ApiResponse<T> getApiResponse(String response, TypeToken<ApiResponseWrapper<T>> responseType)
    {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new DateTypeAdapter());

        Gson gson = builder.create();
        ApiResponseWrapper<T> responseWrapper = gson.fromJson(response, responseType.getType());

        return responseWrapper.getResponse();
    }

    private String buildParamsQuery(NameValuePair... nameValuePairs)
    {
        List<NameValuePair> qparams = getRequiredParams();

        for (NameValuePair nameValuePair : nameValuePairs)
            if (nameValuePair.getValue() != null)
                qparams.add(nameValuePair);

        return URLEncodedUtils.format(qparams, DEFAULT_ENCODING);
    }

    private List<NameValuePair> getRequiredParams()
    {
        List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair(API_KEY, apiKey));
        qparams.add(new BasicNameValuePair(PROJECT_ID, projectId));

        return qparams;
    }

    private String maskApiKey(String apiKey)
    {
        return String.format(API_KEY_MASK, apiKey.substring(0, apiKey.lastIndexOf("-")));
    }

    private String getApiResponseMessages(ApiResponse apiResponse)
    {
        String responseMessages = StringUtils.EMPTY;

        if (!SUCCESS_CODE.equals(apiResponse.getCode()))
            responseMessages = String.format(RESPONSE_MESSAGES, StringUtils.join(apiResponse.getMessages(), ", "));

        return responseMessages;
    }
}
