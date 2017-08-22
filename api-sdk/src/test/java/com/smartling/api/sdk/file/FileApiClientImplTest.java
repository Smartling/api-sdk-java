package com.smartling.api.sdk.file;

import com.smartling.api.sdk.ProxyConfiguration;
import com.smartling.api.sdk.auth.AuthenticationToken;
import com.smartling.api.sdk.auth.TokenProvider;
import com.smartling.api.sdk.dto.file.FileLastModified;
import com.smartling.api.sdk.dto.file.StringResponse;
import com.smartling.api.sdk.dto.file.UploadFileData;
import com.smartling.api.sdk.exceptions.SmartlingApiException;
import com.smartling.api.sdk.file.parameters.FileImportParameterBuilder;
import com.smartling.api.sdk.file.parameters.FileLastModifiedParameterBuilder;
import com.smartling.api.sdk.file.parameters.FileListSearchParameterBuilder;
import com.smartling.api.sdk.file.parameters.FileUploadParameterBuilder;
import com.smartling.api.sdk.file.parameters.GetFileParameterBuilder;
import com.smartling.api.sdk.file.parameters.GetOriginalFileParameterBuilder;
import com.smartling.api.sdk.file.response.EmptyResponse;
import com.smartling.api.sdk.file.response.FileImportSmartlingData;
import com.smartling.api.sdk.file.response.FileList;
import com.smartling.api.sdk.file.response.FileLocaleStatus;
import com.smartling.api.sdk.file.response.FileStatus;
import com.smartling.api.sdk.util.DateFormatter;
import com.smartling.api.sdk.util.HttpUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;

import static mockit.Deencapsulation.setField;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FileApiClientImplTest
{
    private static final String CALLBACK_URL = "callbackUrl";
    private static final String PROJECT_ID = "testProject";
    private static final String LOCALE = "en-US";
    private static final String FILE_URI = "fileUri";
    private static final String FILE_URI2 = "fileUri2";
    private static final String CHARSET = "UTF-8";

    private static final String EXPECTED_AUTHORIZATION_HEADER = "BEARER <token>";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private FileApiClient fileApiClient;

    @Captor
    private ArgumentCaptor<HttpRequestBase> requestCaptor;

    @Mock
    private StringResponse     response;
    @Mock
    private HttpUtils          httpUtils;
    @Mock
    private TokenProvider      tokenProvider;
    @Mock
    private ProxyConfiguration proxyConfiguration;

    @Before
    public void setup() throws SmartlingApiException
    {
        when(tokenProvider.getAuthenticationToken()).thenReturn(new AuthenticationToken("BEARER", "<token>"));
        when(httpUtils.executeHttpCall(requestCaptor.capture(), eq(proxyConfiguration))).thenReturn(response);
        when(response.isSuccess()).thenReturn(true);

        fileApiClient = new FileApiClientImpl.Builder(PROJECT_ID)
                .proxyConfiguration(proxyConfiguration)
                .withCustomTokenProvider(tokenProvider)
                .build();
        setField(fileApiClient, "httpUtils", httpUtils);
    }

    @Test
    public void testUploadFile() throws Exception
    {
        when(response.getContents()).thenReturn(ResponseExamples.UPLOAD_RESPONSE);

        UploadFileData uploadFileDataResponse = fileApiClient.uploadFile(mock(File.class), newUploadParameters());

        HttpRequestBase request = requestCaptor.getValue();
        assertEquals(EXPECTED_AUTHORIZATION_HEADER, request.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
        assertEquals(HttpPost.class, request.getClass());
        assertNotNull(((HttpPost)request).getEntity());
        assertEquals("https://api.smartling.com/files-api/v2/projects/testProject/file", request.getURI().toString());
        assertEquals(false, uploadFileDataResponse.isOverWritten());
        assertEquals(1, uploadFileDataResponse.getStringCount());
        assertEquals(2, uploadFileDataResponse.getWordCount());
    }

    @Test
    public void testUploadFileFromStream() throws Exception
    {
        when(response.getContents()).thenReturn(ResponseExamples.UPLOAD_RESPONSE);

        fileApiClient.uploadFile(mock(InputStream.class), FILE_URI, newUploadParameters());

        HttpRequestBase request = requestCaptor.getValue();
        assertEquals(EXPECTED_AUTHORIZATION_HEADER, request.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
        assertEquals(HttpPost.class, request.getClass());
        assertNotNull(((HttpPost)request).getEntity());
        assertEquals("https://api.smartling.com/files-api/v2/projects/testProject/file", request.getURI().toString());
    }

    @Test
    public void testDeleteFile() throws Exception
    {
        String fileUriWithSpecialSymbols = "©¥†çƒ";
        when(response.getContents()).thenReturn(ResponseExamples.EMPTY_RESPONSE);

        EmptyResponse apiResponse = fileApiClient.deleteFile(fileUriWithSpecialSymbols);

        HttpRequestBase request = requestCaptor.getValue();
        assertEquals(EXPECTED_AUTHORIZATION_HEADER, request.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
        assertEquals("https://api.smartling.com/files-api/v2/projects/testProject/file/delete", request.getURI().toString());
        assertEquals(HttpPost.class, request.getClass());
        HttpPost postRequest = (HttpPost) request;
        assertThat(postRequest.getEntity(), instanceOf(StringEntity.class));
        assertThat(postRequest.getEntity().getContentEncoding().getValue(), equalTo("UTF-8"));
        assertThat(postRequest.getEntity().getContentType().getValue(), startsWith("application/json"));
        assertThat(IOUtils.toString(postRequest.getEntity().getContent(), "UTF-8"), containsString(fileUriWithSpecialSymbols));
        assertEquals(EmptyResponse.class, apiResponse.getClass());
    }

    @Test
    public void testRenameFile() throws Exception
    {
        when(response.getContents()).thenReturn(ResponseExamples.EMPTY_RESPONSE);

        EmptyResponse apiResponse = fileApiClient.renameFile(FILE_URI, FILE_URI2);

        HttpRequestBase request = requestCaptor.getValue();
        assertEquals(EXPECTED_AUTHORIZATION_HEADER, request.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
        assertEquals(HttpPost.class, request.getClass());
        assertEquals("https://api.smartling.com/files-api/v2/projects/testProject/file/rename", request.getURI().toString());
        assertEquals(EmptyResponse.class, apiResponse.getClass());
    }

    @Test
    public void testGetLastModified() throws Exception
    {
        when(response.getContents()).thenReturn(ResponseExamples.LAST_MODIFICATION_RESPONSE);

        FileLastModified apiResponse = fileApiClient.getLastModified(new FileLastModifiedParameterBuilder(FILE_URI));

        HttpRequestBase request = requestCaptor.getValue();
        assertEquals(EXPECTED_AUTHORIZATION_HEADER, request.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
        assertEquals(HttpGet.class, request.getClass());
        assertEquals("https://api.smartling.com/files-api/v2/projects/testProject/file/last-modified?fileUri=fileUri", request.getURI().toString());
        assertEquals(5, apiResponse.getItems().size());
        assertEquals(5, apiResponse.getTotalCount());
        assertEquals("be-BY", apiResponse.getItems().get(0).getLocaleId());
        assertEquals(DateFormatter.parse("2015-09-15T21:24:42+0000").getTime(), apiResponse.getItems().get(0).getLastModified().getTime());
    }

    @Test
    public void testGetLastModifiedWhenGetWrongResponse() throws Exception
    {
        when(response.getContents()).thenReturn(ResponseExamples.NOT_EXISTING_CODE_RESPONSE);

        expectedException.expect(SmartlingApiException.class);
        expectedException.expectMessage("Response hasn't been parsed correctly [response=");

        fileApiClient.getLastModified(new FileLastModifiedParameterBuilder(FILE_URI));
    }

    @Test
    public void testGetLastModifiedShouldThrowSmartlingApiExceptionWhenEmptyContent() throws Exception
    {
        when(response.getContents()).thenReturn("");

        expectedException.expect(SmartlingApiException.class);
        expectedException.expectMessage("Response hasn't been parsed correctly [response=");

        fileApiClient.getLastModified(new FileLastModifiedParameterBuilder(FILE_URI));
    }

    @Test
    public void testGetLastModifiedShouldThrowSmartlingApiExceptionWhenInvalidJson() throws Exception
    {
        when(response.getContents()).thenReturn("<b>This is not JSON</b>");

        expectedException.expect(SmartlingApiException.class);
        expectedException.expectMessage("Can't parse response as JSON [response=");

        fileApiClient.getLastModified(new FileLastModifiedParameterBuilder(FILE_URI));
    }

    @Test
    public void testGetFile() throws Exception
    {
        when(response.getContents()).thenReturn(ResponseExamples.GET_FILE_RESPONSE);

        fileApiClient.getFile(new GetFileParameterBuilder(FILE_URI, LOCALE));

        HttpRequestBase request = requestCaptor.getValue();
        assertEquals("https://api.smartling.com/files-api/v2/projects/testProject/locales/" + LOCALE + "/file?fileUri=fileUri", request.getURI().toString());
    }

    @Test
    public void testGetOriginalFile() throws Exception
    {
        when(response.getContents()).thenReturn(ResponseExamples.GET_FILE_RESPONSE);

        fileApiClient.getOriginalFile(new GetOriginalFileParameterBuilder(FILE_URI));

        HttpRequestBase request = requestCaptor.getValue();
        assertEquals("https://api.smartling.com/files-api/v2/projects/testProject/file?fileUri=fileUri", request.getURI().toString());
    }

    @Test
    public void testShouldThrowSmartlingApiExceptionWithCodeAndDetailsIfCodeIsNotAcceptedOrSuccessWhenGetFile() throws Exception
    {
        when(response.isSuccess()).thenReturn(false);
        when(response.getContents()).thenReturn(ResponseExamples.ERROR_RESPONSE);

        expectedException.expect(SmartlingApiException.class);
        expectedException.expectMessage("MAINTENANCE_MODE_ERROR\n" +
                "Error{key='some_error', message='Some error', details=ErrorDetails{field='null', errorId='null'}}");

        fileApiClient.getFile(new GetFileParameterBuilder(FILE_URI, LOCALE));
    }

    @Test
    public void testShouldThrowSmartlingApiExceptionWithCodeAndDetailsIfCodeIsNotAcceptedOrSuccessWhenGetOriginalFile() throws Exception
    {
        when(response.isSuccess()).thenReturn(false);
        when(response.getContents()).thenReturn(ResponseExamples.ERROR_RESPONSE);

        expectedException.expect(SmartlingApiException.class);
        expectedException.expectMessage("MAINTENANCE_MODE_ERROR\n" +
                "Error{key='some_error', message='Some error', details=ErrorDetails{field='null', errorId='null'}}");

        fileApiClient.getOriginalFile(new GetOriginalFileParameterBuilder(FILE_URI));
    }

    @Test
    public void testGetFilesList() throws Exception
    {
        when(response.getContents()).thenReturn(ResponseExamples.FILE_LIST_RESPONSE);

        FileList apiResponse = fileApiClient.getFilesList(new FileListSearchParameterBuilder());

        HttpRequestBase request = requestCaptor.getValue();
        assertEquals(EXPECTED_AUTHORIZATION_HEADER, request.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
        assertEquals(HttpGet.class, request.getClass());
        assertEquals("https://api.smartling.com/files-api/v2/projects/testProject/files/list?", request.getURI().toString());
        assertEquals(2, apiResponse.getItems().size());
        assertEquals(2, apiResponse.getTotalCount());
        assertEquals("FileListItem[fileUri=3-namespace-explicit.xml,lastUploaded=2015-07-29T10:34:30+0000,fileType=xml]", apiResponse.getItems().get(0).toString());
    }

    @Test
    public void testGetFileLocaleStatus() throws Exception
    {
        when(response.getContents()).thenReturn(ResponseExamples.FILE_LOCALES_STATUS_RESPONSE);

        FileLocaleStatus apiResponse = fileApiClient.getFileLocaleStatus(FILE_URI, LOCALE);

        HttpRequestBase request = requestCaptor.getValue();
        assertEquals(EXPECTED_AUTHORIZATION_HEADER, request.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
        assertEquals(HttpGet.class, request.getClass());
        assertEquals("https://api.smartling.com/files-api/v2/projects/testProject/locales/en-US/file/status?fileUri=fileUri", request.getURI().toString());
        assertEquals(
                "FileLocaleStatus[fileUri=test-cancel-translation.xml,totalStringCount=1,totalWordCount=3,authorizedStringCount=0,authorizedWordCount=0,completedStringCount=0,"
                        + "completedWordCount=0,excludedStringCount=0,excludedWordCount=0,lastUploaded=2015-09-15T21:28:25+0000,fileType=xml]",
                apiResponse.toString()
        );
    }

    @Test
    public void testGetFileStatus() throws Exception
    {
        when(response.getContents()).thenReturn(ResponseExamples.FILE_STATUS_RESPONSE);

        FileStatus apiResponse = fileApiClient.getFileStatus(FILE_URI);

        HttpRequestBase request = requestCaptor.getValue();
        assertEquals(EXPECTED_AUTHORIZATION_HEADER, request.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
        assertEquals(HttpGet.class, request.getClass());
        assertEquals("https://api.smartling.com/files-api/v2/projects/testProject/file/status?fileUri=fileUri", request.getURI().toString());
        assertEquals(5, apiResponse.getItems().size());
        assertEquals(5, apiResponse.getTotalCount());
        assertEquals(3, apiResponse.getTotalWordCount());
        assertEquals(1, apiResponse.getTotalStringCount());
        assertEquals(
                "FileStatusItem[localeId=be-BY,authorizedStringCount=0,authorizedWordCount=0,completedStringCount=0,completedWordCount=0,excludedStringCount=0,"
                        + "excludedWordCount=0]",
                apiResponse.getItems().get(0).toString()
        );
    }

    @Test
    public void testImportTranslations() throws Exception
    {
        when(response.getContents()).thenReturn(ResponseExamples.IMPORT_TRANSLATIONS_RESPONSE);

        FileImportSmartlingData apiResponse = fileApiClient
                .importTranslations(new FileImportParameterBuilder(mock(File.class), LOCALE, CHARSET, FileType.CSV, FILE_URI));

        HttpRequestBase request = requestCaptor.getValue();
        assertEquals(EXPECTED_AUTHORIZATION_HEADER, request.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
        assertEquals(HttpPost.class, request.getClass());
        assertEquals("https://api.smartling.com/files-api/v2/projects/testProject/locales/en-US/file/import", request.getURI().toString());
        assertEquals(0, apiResponse.getStringCount());
        assertEquals(0, apiResponse.getTranslationImportErrors().size());
    }

    private static FileUploadParameterBuilder newUploadParameters()
    {
        FileUploadParameterBuilder fileUploadParameterBuilder = new FileUploadParameterBuilder(FileType.JAVA_PROPERTIES, FILE_URI);
        fileUploadParameterBuilder.approveContent(true);
        fileUploadParameterBuilder.callbackUrl(CALLBACK_URL);
        fileUploadParameterBuilder.localeIdsToAuthorize(Collections.singletonList(LOCALE));
        fileUploadParameterBuilder.overwriteAuthorizedLocales(true);
        fileUploadParameterBuilder.charset(CHARSET);

        return fileUploadParameterBuilder;
    }
}