package com.smartling.api.sdk.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
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
    public static final String DEFAULT_ENCODING    = "UTF-8";

    private static String      uploadFileApiUrl    = "%s/file/upload?";
    private static String      getFileApiUrl       = "%s/file/get?";
    private static String      getFilesListApiUrl  = "%s/file/list?";
    private static String      getFileStatusApiUrl = "%s/file/status?";

    private String             baseApiUrl;
    private String             apiKey;
    private String             projectId;

    public FileApiClientAdapterImpl(String baseApiUrl, String apiKey, String projectId)
    {
        this.baseApiUrl = baseApiUrl;
        this.apiKey = apiKey;
        this.projectId = projectId;
    }

    public String uploadFile(String fileType, String fileUri, String fileName, String fileEncoding) throws FileApiException
    {
        StringBuffer uploadFileParameters = new StringBuffer();
        uploadFileParameters.append("apiKey=").append(apiKey).append("&projectId=").append(projectId).append("&fileUri=").append(fileUri).append("&fileType=").append(fileType);

        return doPostRequest(uploadFileParameters.toString(), fileName, fileEncoding);
    }

    public String getFile(String fileUri, String locale) throws FileApiException
    {
        StringBuffer getFileParameters = new StringBuffer();
        getFileParameters.append("apiKey=").append(apiKey).append("&projectId=").append(projectId).append("&fileUri=").append(fileUri);

        if (StringUtils.isNotBlank(locale))
            getFileParameters.append("&locale=").append(locale);

        return doGetRequest(getFileApiUrl, getFileParameters.toString());
    }

    public String getFilesList() throws FileApiException
    {
        StringBuffer getFilesListParameters = new StringBuffer();
        getFilesListParameters.append("apiKey=").append(apiKey).append("&projectId=").append(projectId);

        return doGetRequest(getFilesListApiUrl, getFilesListParameters.toString());
    }

    public String getFilesList(String locale) throws FileApiException
    {
        StringBuffer getFilesListParameters = new StringBuffer();
        getFilesListParameters.append("apiKey=").append(apiKey).append("&projectId=").append(projectId).append("&locale=").append(locale);

        return doGetRequest(getFilesListApiUrl, getFilesListParameters.toString());
    }

    public String getFileStatus(String fileUri, String locale) throws FileApiException
    {
        StringBuffer getFileStatusParameters = new StringBuffer();
        getFileStatusParameters.append("apiKey=").append(apiKey).append("&projectId=").append(projectId).append("&fileUri=").append(fileUri).append("&locale=").append(locale);

        return doGetRequest(getFileStatusApiUrl, getFileStatusParameters.toString());
    }

    private String doPostRequest(String apiParameters, String filePath, String fileEncoding) throws FileApiException
    {
        File file = new File(filePath);

        HttpPost httpPost = new HttpPost(String.format(uploadFileApiUrl, baseApiUrl) + apiParameters);

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
                return inputStreamToString(urlConnection.getInputStream(), urlConnection.getContentEncoding());

            throw new FileApiException(inputStreamToString(urlConnection.getInputStream(), urlConnection.getContentEncoding()));
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
            IOUtils.copy(inputStream, writer, encoding != null ? encoding : DEFAULT_ENCODING);
        }
        catch (IOException e)
        {
            throw new FileApiException(e);
        }

        return writer.toString();
    }
}