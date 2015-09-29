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
package com.smartling.api.sdk;

import com.smartling.api.sdk.dto.ApiResponse;
import com.smartling.api.sdk.dto.EmptyResponse;
import com.smartling.api.sdk.dto.file.FileLastModified;
import com.smartling.api.sdk.dto.file.FileList;
import com.smartling.api.sdk.dto.file.FileLocaleLastModified;
import com.smartling.api.sdk.dto.file.FileStatus;
import com.smartling.api.sdk.dto.file.StringResponse;
import com.smartling.api.sdk.dto.file.UploadFileData;
import com.smartling.api.sdk.exceptions.ApiException;
import com.smartling.api.sdk.file.FileApiParams;
import com.smartling.api.sdk.file.FileListSearchParams;
import com.smartling.api.sdk.file.FileType;
import com.smartling.api.sdk.file.RetrievalType;
import com.smartling.api.sdk.file.parameters.FileUploadParameterBuilder;
import com.smartling.api.sdk.util.DateFormatter;
import com.smartling.api.sdk.util.HttpUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.smartling.api.sdk.dto.ApiCode.SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FileApiClientAdapterTest
{
    private static final String CALLBACK_URL = "callbackUrl";
    private static final String API_KEY = "apiKeyValue";
    private static final String PROJECT_ID = "projectIdValue";
    private static final String LOCALE = "en-US";
    private static final String FILE_URI = "fileUri";
    private static final String FILE_URI2 = "fileUri2";
    private static final String HOST = "host";
    private static final String BASE_URL = "http://" + HOST;
    private static final String FILE_LIST_RESPONSE = "{\"response\":{\"data\":{\"fileCount\": 1, \"fileList\": [{\"fileUri\": \"fileUri\", \"stringCount\": 2, \"wordCount\": 3, \"approvedStringCount\": 4, \"completedStringCount\": 5, \"lastUploaded\": \"lastDate\", \"fileType\": \"JAVA_PROPERTIES\", \"callbackUrl\": \"callbackUrl\"}]},\"code\":\"SUCCESS\",\"messages\":[]}}";
    private static final String EMPTY_SUCESS_RESPONSE = "{\"response\":{\"data\": null,\"code\":\"SUCCESS\",\"messages\":[]}}";
    private static final String LAST_MODIFIED_RESPONSE = "{\"response\":{\"data\": {\"items\": [{\"locale\": \"%s\", \"lastModified\": \"%s\"}]}, \"code\":\"SUCCESS\", \"messages\":[]}}";
    private static final String UPLOAD_RESPONSE = "{\"response\":{\"data\": {\"stringCount\": 1, \"wordCount\": 2, \"overWritten\": true},\"code\":\"SUCCESS\",\"messages\":[]}}";

    private FileApiClientAdapterImpl fileApiClientAdapter;
    private HttpUtils httpUtils;
    private ProxyConfiguration proxyConfiguration;

    @Before
    public void setup()
    {
        proxyConfiguration = mock(ProxyConfiguration.class);
        fileApiClientAdapter = new FileApiClientAdapterImpl(BASE_URL, API_KEY, PROJECT_ID, proxyConfiguration);
        fileApiClientAdapter.setHttpUtils(httpUtils = mock(HttpUtils.class));
    }

    @Test(expected = NullPointerException.class)
    public void testInvalidConstructor()
    {
        fileApiClientAdapter = new FileApiClientAdapterImpl(null, null);
    }

    @Test
    public void testFileGet() throws ApiException, IOException
    {
        fileApiClientAdapter.getFile(FILE_URI, LOCALE, RetrievalType.PUBLISHED);

        ArgumentCaptor<HttpRequestBase> requestCaptor = ArgumentCaptor.forClass(HttpRequestBase.class);

        verify(httpUtils).executeHttpCall(requestCaptor.capture(), eq(proxyConfiguration));

        HttpRequestBase request = requestCaptor.getValue();

        List<NameValuePair> params = URLEncodedUtils.parse(request.getURI(), "UTF-8");
        assertTrue(params.contains(new BasicNameValuePair(FileApiParams.API_KEY, API_KEY)));
        assertTrue(params.contains(new BasicNameValuePair(FileApiParams.PROJECT_ID, PROJECT_ID)));
        assertTrue(params.contains(new BasicNameValuePair(FileApiParams.LOCALE, LOCALE)));
        assertTrue(params.contains(new BasicNameValuePair(FileApiParams.FILE_URI, FILE_URI)));
        assertEquals(HOST, request.getURI().getHost());
    }

    @Test
    public void testFileGetList() throws ApiException, IOException
    {
        String uriMask = "URI_MASK";
        Date lastUploadedAfter = new Date();
        Date lastUploadedBefore = new Date();
        List<String> conditions = Collections.singletonList("CONDITION1");
        List<String> orderBy = Collections.singletonList("ORDER1");

        FileListSearchParams fileListSearchParams = new FileListSearchParams();
        fileListSearchParams.setLimit(12);
        fileListSearchParams.setLastUploadedAfter(lastUploadedAfter);
        fileListSearchParams.setLocale(LOCALE);
        fileListSearchParams.setOffset(3);
        fileListSearchParams.setFileTypes(Collections.singletonList(FileType.JAVA_PROPERTIES.name()));
        fileListSearchParams.setConditions(conditions);
        fileListSearchParams.setOrderBy(orderBy);
        fileListSearchParams.setUriMask(uriMask);
        fileListSearchParams.setLastUploadedBefore(lastUploadedBefore);

        StringResponse response = mock(StringResponse.class);
        when(response.getContents()).thenReturn(FILE_LIST_RESPONSE);

        ArgumentCaptor<HttpRequestBase> requestCaptor = ArgumentCaptor.forClass(HttpRequestBase.class);
        when(httpUtils.executeHttpCall(requestCaptor.capture(), eq(proxyConfiguration))).thenReturn(response);

        ApiResponse<FileList> apiResponse = fileApiClientAdapter.getFilesList(fileListSearchParams);

        // Validate the request
        HttpRequestBase request = requestCaptor.getValue();
        List<NameValuePair> params = URLEncodedUtils.parse(request.getURI(), "UTF-8");
        assertTrue(params.contains(new BasicNameValuePair(FileApiParams.API_KEY, API_KEY)));
        assertTrue(params.contains(new BasicNameValuePair(FileApiParams.PROJECT_ID, PROJECT_ID)));
        assertTrue(params.contains(new BasicNameValuePair(FileApiParams.LOCALE, LOCALE)));
        assertTrue(params.contains(new BasicNameValuePair(FileApiParams.LIMIT, "12")));
        assertTrue(params.contains(new BasicNameValuePair(FileApiParams.LAST_UPLOADED_AFTER, DateFormatter.format(lastUploadedAfter))));
        assertTrue(params.contains(new BasicNameValuePair(FileApiParams.LAST_UPLOADED_BEFORE, DateFormatter.format(lastUploadedBefore))));
        assertTrue(params.contains(new BasicNameValuePair(FileApiParams.OFFSET, "3")));
        assertTrue(params.contains(new BasicNameValuePair(FileApiParams.FILE_TYPES, FileType.JAVA_PROPERTIES.name())));
        assertTrue(params.contains(new BasicNameValuePair(FileApiParams.CONDITIONS, "CONDITION1")));
        assertTrue(params.contains(new BasicNameValuePair(FileApiParams.ORDERBY, "ORDER1")));
        assertTrue(params.contains(new BasicNameValuePair(FileApiParams.URI_MASK, "URI_MASK")));
        assertEquals(HOST, request.getURI().getHost());

        // Validate the response
        FileList fileList = apiResponse.getData();
        assertEquals(1, fileList.getFileCount());
        assertEquals(1, fileList.getFileList().size());

        FileStatus status = fileList.getFileList().get(0);
        assertEquals(2, status.getStringCount());
        assertEquals(3, status.getWordCount());
        assertEquals("fileUri", status.getFileUri());
        assertEquals("JAVA_PROPERTIES", status.getFileType());
        assertEquals(4, status.getApprovedStringCount());
        assertEquals(5, status.getCompletedStringCount());
        assertEquals("lastDate", status.getLastUploaded());
        assertEquals("callbackUrl", status.getCallbackUrl());
    }

    @Test
    public void testRenameFile() throws ApiException, IOException
    {
        StringResponse response = mock(StringResponse.class);
        when(response.getContents()).thenReturn(EMPTY_SUCESS_RESPONSE);

        ArgumentCaptor<HttpRequestBase> requestCaptor = ArgumentCaptor.forClass(HttpRequestBase.class);
        when(httpUtils.executeHttpCall(requestCaptor.capture(), eq(proxyConfiguration))).thenReturn(response);

        ApiResponse<EmptyResponse> apiResponse = fileApiClientAdapter.renameFile(FILE_URI, FILE_URI2);

        // Validate the request
        HttpRequestBase request = requestCaptor.getValue();
        List<NameValuePair> params = URLEncodedUtils.parse(request.getURI(), "UTF-8");
        assertTrue(params.contains(new BasicNameValuePair(FileApiParams.API_KEY, API_KEY)));
        assertTrue(params.contains(new BasicNameValuePair(FileApiParams.PROJECT_ID, PROJECT_ID)));
        assertTrue(params.contains(new BasicNameValuePair(FileApiParams.FILE_URI, FILE_URI)));
        assertTrue(params.contains(new BasicNameValuePair(FileApiParams.NEW_FILE_URI, FILE_URI2)));
        assertEquals(HOST, request.getURI().getHost());

        // Validate the response
        assertEquals(SUCCESS, apiResponse.getCode());
        assertNull(apiResponse.getData());
    }

    @Test
    public void testDeleteFile() throws ApiException, IOException
    {
        StringResponse response = mock(StringResponse.class);
        when(response.getContents()).thenReturn(EMPTY_SUCESS_RESPONSE);

        ArgumentCaptor<HttpRequestBase> requestCaptor = ArgumentCaptor.forClass(HttpRequestBase.class);
        when(httpUtils.executeHttpCall(requestCaptor.capture(), eq(proxyConfiguration))).thenReturn(response);

        ApiResponse<EmptyResponse> apiResponse = fileApiClientAdapter.deleteFile(FILE_URI);

        // Validate the request
        HttpRequestBase request = requestCaptor.getValue();
        List<NameValuePair> params = URLEncodedUtils.parse(request.getURI(), "UTF-8");
        assertTrue(params.contains(new BasicNameValuePair(FileApiParams.API_KEY, API_KEY)));
        assertTrue(params.contains(new BasicNameValuePair(FileApiParams.PROJECT_ID, PROJECT_ID)));
        assertTrue(params.contains(new BasicNameValuePair(FileApiParams.FILE_URI, FILE_URI)));
        assertEquals(HOST, request.getURI().getHost());

        // Validate the response
        assertEquals(SUCCESS, apiResponse.getCode());
        assertNull(apiResponse.getData());
    }

    @Test
    public void testLastModified() throws ApiException, IOException
    {
        Date lastModifiedAfter = new Date();

        StringResponse response = mock(StringResponse.class);
        when(response.getContents()).thenReturn(String.format(LAST_MODIFIED_RESPONSE, LOCALE, DateFormatter.format(lastModifiedAfter)));

        ArgumentCaptor<HttpRequestBase> requestCaptor = ArgumentCaptor.forClass(HttpRequestBase.class);
        when(httpUtils.executeHttpCall(requestCaptor.capture(), eq(proxyConfiguration))).thenReturn(response);

        ApiResponse<FileLastModified> apiResponse = fileApiClientAdapter.getLastModified(FILE_URI, lastModifiedAfter, LOCALE);

        // Validate the request
        HttpRequestBase request = requestCaptor.getValue();
        List<NameValuePair> params = URLEncodedUtils.parse(request.getURI(), "UTF-8");
        assertTrue(params.contains(new BasicNameValuePair(FileApiParams.API_KEY, API_KEY)));
        assertTrue(params.contains(new BasicNameValuePair(FileApiParams.PROJECT_ID, PROJECT_ID)));
        assertTrue(params.contains(new BasicNameValuePair(FileApiParams.FILE_URI, FILE_URI)));
        assertTrue(params.contains(new BasicNameValuePair(FileApiParams.LAST_MODIFIED_AFTER, DateFormatter.format(lastModifiedAfter))));
        assertTrue(params.contains(new BasicNameValuePair(FileApiParams.LOCALE, LOCALE)));
        assertEquals(HOST, request.getURI().getHost());

        // Validate the response
        assertEquals(SUCCESS, apiResponse.getCode());
        assertNotNull(apiResponse.getData());

        FileLastModified fileLastModified = apiResponse.getData();
        assertEquals(1, fileLastModified.getItems().size());

        FileLocaleLastModified fileLocaleLastModified = fileLastModified.getItems().get(0);
        assertEquals(lastModifiedAfter.getTime() / 1000, fileLocaleLastModified.getLastModified().getTime() / 1000);
        assertEquals(LOCALE, fileLocaleLastModified.getLocale());
    }

    @Test
    public void testUploadFile() throws ApiException, IOException
    {
        FileUploadParameterBuilder fileUploadParameterBuilder = new FileUploadParameterBuilder();
        fileUploadParameterBuilder.approveContent(true);
        fileUploadParameterBuilder.callbackUrl(CALLBACK_URL);
        fileUploadParameterBuilder.fileType(FileType.JAVA_PROPERTIES);
        fileUploadParameterBuilder.localesToApprove(Collections.singletonList(LOCALE));
        fileUploadParameterBuilder.overwriteApprovedLocales(true);
        fileUploadParameterBuilder.fileUri(FILE_URI);

        File uploadFile = mock(File.class);
        when(uploadFile.getName()).thenReturn(FILE_URI);

        StringResponse response = mock(StringResponse.class);
        when(response.getContents()).thenReturn(UPLOAD_RESPONSE);

        ArgumentCaptor<HttpRequestBase> requestCaptor = ArgumentCaptor.forClass(HttpRequestBase.class);
        when(httpUtils.executeHttpCall(requestCaptor.capture(), eq(proxyConfiguration))).thenReturn(response);

        ApiResponse<UploadFileData> apiResponse = fileApiClientAdapter.uploadFile(uploadFile, "UTF-8", fileUploadParameterBuilder);

        // Validate the request
        HttpRequestBase request = requestCaptor.getValue();
        List<NameValuePair> params = URLEncodedUtils.parse(request.getURI(), "UTF-8");
        assertTrue(params.contains(new BasicNameValuePair(FileApiParams.API_KEY, API_KEY)));
        assertTrue(params.contains(new BasicNameValuePair(FileApiParams.PROJECT_ID, PROJECT_ID)));
        assertTrue(params.contains(new BasicNameValuePair(FileApiParams.CALLBACK_URL, CALLBACK_URL)));
        assertTrue(params.contains(new BasicNameValuePair(FileApiParams.FILE_URI, FILE_URI)));
        assertTrue(params.contains(new BasicNameValuePair(FileApiParams.FILE_TYPE, FileType.JAVA_PROPERTIES.getIdentifier())));
        assertTrue(params.contains(new BasicNameValuePair(FileApiParams.LOCALES_TO_APPROVE + "[0]", LOCALE)));
        assertTrue(params.contains(new BasicNameValuePair(FileApiParams.APPROVED, Boolean.TRUE.toString())));
        assertTrue(params.contains(new BasicNameValuePair(FileApiParams.OVERWRITE_APPROVED_LOCALES, Boolean.TRUE.toString())));
        assertEquals(HOST, request.getURI().getHost());

        // Validate the response
        assertEquals(SUCCESS, apiResponse.getCode());
        UploadFileData uploadFileData = apiResponse.getData();
        assertEquals(1, uploadFileData.getStringCount());
        assertEquals(2, uploadFileData.getWordCount());
        assertTrue(uploadFileData.isOverWritten());
    }
}