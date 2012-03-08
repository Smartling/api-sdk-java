package com.smartling.api.sdk.file;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smartling.api.sdk.file.response.ApiResponse;
import com.smartling.api.sdk.file.response.ApiResponseWrapper;
import com.smartling.api.sdk.file.response.FileList;
import com.smartling.api.sdk.file.response.FileStatus;
import com.smartling.api.sdk.file.response.UploadData;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

public class FileApiClientAdapterImpl implements FileApiClientAdapter
{
    private static final String UTF_16                  = "UTF-16";

    public static final String  DEFAULT_ENCODING        = "UTF-8";

    private final static String UPLOAD_FILE_API_URL     = "%s/file/upload?";
    private final static String GET_FILE_API_URL        = "%s/file/get?";
    private final static String GET_FILE_LIST_API_URL   = "%s/file/list?";
    private final static String GET_FILE_STATUS_API_URL = "%s/file/status?";

    private String              baseApiUrl;
    private String              apiKey;
    private String              projectId;

    public FileApiClientAdapterImpl(String baseApiUrl, String apiKey, String projectId)
    {
        this.baseApiUrl = baseApiUrl;
        this.apiKey = apiKey;
        this.projectId = projectId;
    }

    public ApiResponse<UploadData> uploadFile(String fileType, String fileUri, String fileName, String fileEncoding) throws FileApiException
    {
        StringBuffer uploadFileParameters = new StringBuffer();
        uploadFileParameters.append("apiKey=").append(apiKey).append("&projectId=").append(projectId).append("&fileUri=").append(fileUri).append("&fileType=").append(fileType);

        String response = doPostRequest(uploadFileParameters.toString(), fileName, fileEncoding);
        return getApiResponse(response, new TypeToken<ApiResponseWrapper<UploadData>>() {}.getType());
    }

    public String getFile(String fileUri, String locale) throws FileApiException
    {
        StringBuffer getFileParameters = new StringBuffer();
        getFileParameters.append("apiKey=").append(apiKey).append("&projectId=").append(projectId).append("&fileUri=").append(fileUri);

        if (StringUtils.isNotBlank(locale))
            getFileParameters.append("&locale=").append(locale);

        return doGetRequest(GET_FILE_API_URL, getFileParameters.toString());
    }

    public ApiResponse<FileList> getFilesList(String locale) throws FileApiException
    {
        StringBuffer getFilesListParameters = new StringBuffer();
        getFilesListParameters.append("apiKey=").append(apiKey).append("&projectId=").append(projectId);

        if (null != locale)
            getFilesListParameters.append("&locale=").append(locale);

        String response = doGetRequest(GET_FILE_LIST_API_URL, getFilesListParameters.toString());
        return getApiResponse(response, new TypeToken<ApiResponseWrapper<FileList>>() {}.getType());
    }

    public ApiResponse<FileStatus> getFileStatus(String fileUri, String locale) throws FileApiException
    {
        StringBuffer getFileStatusParameters = new StringBuffer();
        getFileStatusParameters.append("apiKey=").append(apiKey).append("&projectId=").append(projectId).append("&fileUri=").append(fileUri).append("&locale=").append(locale);

        String response = doGetRequest(GET_FILE_STATUS_API_URL, getFileStatusParameters.toString());
        return getApiResponse(response, new TypeToken<ApiResponseWrapper<FileStatus>>() {}.getType());
    }

    private String doPostRequest(String apiParameters, String filePath, String fileEncoding) throws FileApiException
    {
        File file = new File(filePath);

        HttpPost httpPost = new HttpPost(String.format(UPLOAD_FILE_API_URL, baseApiUrl) + apiParameters);

        MultipartEntity mpEntity = new MultipartEntity();
        ContentBody cbFile = new FileBody(file, "file", "text/plain", fileEncoding);
        mpEntity.addPart("file", cbFile);

        httpPost.setEntity(mpEntity);

        HttpResponse response = null;
        try
        {
            response = new DefaultHttpClient().execute(httpPost);

            if (HttpServletResponse.SC_OK == response.getStatusLine().getStatusCode())
                return inputStreamToString(response.getEntity().getContent(), null);

            throw new FileApiException(inputStreamToString(response.getEntity().getContent(), null));
        }
        catch (IOException e)
        {
            throw new FileApiException(e);
        }
    }

    private String doGetRequest(String apiServerUrl, String apiParameters) throws FileApiException
    {
        StringBuffer urlWithParameters = new StringBuffer(String.format(apiServerUrl, baseApiUrl));
        urlWithParameters.append(apiParameters);

        HttpURLConnection urlConnection = null;
        try
        {
            URL apiUrl = new URL(urlWithParameters.toString());

            urlConnection = (HttpURLConnection)apiUrl.openConnection();

            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpServletResponse.SC_OK)
                return inputStreamToString(urlConnection.getInputStream(), urlConnection.getContentType());

            throw new FileApiException(inputStreamToString(urlConnection.getInputStream(), urlConnection.getContentType()));
        }
        catch (IOException e)
        {
            if (null != urlConnection)
                throw new FileApiException(inputStreamToString(urlConnection.getErrorStream(), null));

            throw new FileApiException(e);
        }
        finally
        {
            if (null != urlConnection)
                urlConnection.disconnect();
        }
    }

    private String inputStreamToString(InputStream inputStream, String encoding) throws FileApiException
    {
        StringWriter writer = new StringWriter();
        try
        {
            // unless UTF-16 explicitly specified, use default UTF-8 encoding.
            IOUtils.copy(inputStream, writer, null == encoding || !encoding.toUpperCase().contains(UTF_16) ? DEFAULT_ENCODING : UTF_16);
        }
        catch (IOException e)
        {
            throw new FileApiException(e);
        }

        return writer.toString();
    }

    @SuppressWarnings("rawtypes")
    private ApiResponse getApiResponse(String response, Type responseType)
    {
        ApiResponseWrapper responseWrapper = new Gson().fromJson(response, responseType);
        return responseWrapper.getResponse();
    }
}