package com.smartling.api.sdk.file;

import com.smartling.api.sdk.HttpClientConfiguration;
import com.smartling.api.sdk.ProxyConfiguration;
import com.smartling.api.sdk.auth.AuthenticationToken;
import com.smartling.api.sdk.auth.TokenProvider;
import com.smartling.api.sdk.dto.file.StringResponse;
import com.smartling.api.sdk.exceptions.SmartlingApiException;
import com.smartling.api.sdk.file.parameters.FileListSearchParameterBuilder;
import com.smartling.api.sdk.file.response.FileList;
import com.smartling.api.sdk.file.response.UidAwareFileListItem;
import com.smartling.api.sdk.util.HttpUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static mockit.Deencapsulation.setField;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountBasedFileApiClientImplTest
{

    private static final String ACCOUNT_UID = "testAccount";

    private static final String EXPECTED_AUTHORIZATION_HEADER = "BEARER <token>";

    private AccountBasedFileApiClient fileApiClient;

    @Captor
    private ArgumentCaptor<HttpRequestBase> requestCaptor;

    @Mock
    private StringResponse response;
    @Mock
    private HttpUtils httpUtils;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private ProxyConfiguration proxyConfiguration;
    @Mock
    private HttpClientConfiguration httpClientConfiguration;

    @Before
    public void setup() throws SmartlingApiException
    {
        when(tokenProvider.getAuthenticationToken()).thenReturn(new AuthenticationToken("BEARER", "<token>"));
        when(httpUtils.executeHttpCall(requestCaptor.capture(), eq(proxyConfiguration), eq(httpClientConfiguration))).thenReturn(response);
        when(response.isSuccess()).thenReturn(true);

        fileApiClient = new AccountBasedFileApiClientImpl.Builder(ACCOUNT_UID)
                .proxyConfiguration(proxyConfiguration)
                .httpClientConfiguration(httpClientConfiguration)
                .withCustomTokenProvider(tokenProvider)
                .build();
        setField(fileApiClient, "httpUtils", httpUtils);
    }


    @Test
    public void testGetFilesList() throws Exception
    {
        when(response.getContents()).thenReturn(ResponseExamples.ACCOUNT_BASED_FILE_LIST_RESPONSE);

        FileList<UidAwareFileListItem> apiResponse = fileApiClient.getFilesList(new FileListSearchParameterBuilder());

        HttpRequestBase request = requestCaptor.getValue();
        assertEquals(EXPECTED_AUTHORIZATION_HEADER, request.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
        assertEquals(HttpGet.class, request.getClass());
        assertEquals("https://api.smartling.com/files-api/v2/accounts/testAccount/files/list?", request.getURI().toString());
        assertEquals(2, apiResponse.getItems().size());
        assertEquals(2, apiResponse.getTotalCount());
        assertEquals("UidAwareFileListItem[fileUri=3-namespace-explicit.xml,fileUid=fileUid1,lastUploaded=2015-07-29T10:34:30+0000,fileType=xml]", apiResponse.getItems().get(0).toString());
    }
}