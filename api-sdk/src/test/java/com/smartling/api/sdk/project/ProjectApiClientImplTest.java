package com.smartling.api.sdk.project;

import com.smartling.api.sdk.ProxyConfiguration;
import com.smartling.api.sdk.auth.AuthenticationToken;
import com.smartling.api.sdk.auth.TokenProvider;
import com.smartling.api.sdk.dto.file.StringResponse;
import com.smartling.api.sdk.exceptions.SmartlingApiException;
import com.smartling.api.sdk.file.parameters.FileLastModifiedParameterBuilder;
import com.smartling.api.sdk.project.response.TargetLocale;
import com.smartling.api.sdk.project.response.ProjectDetails;
import com.smartling.api.sdk.util.HttpUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;

import static mockit.Deencapsulation.setField;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProjectApiClientImplTest
{
    private static final String PROJECT_ID = "2249fadc3";
    private static final String USER_TOKEN = "userSecret BEARER";

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    private ProjectApiClient projectApiClient;
    private StringResponse response;
    private ArgumentCaptor<HttpRequestBase> requestCaptor = ArgumentCaptor.forClass(HttpRequestBase.class);

    @Before
    public void setup() throws SmartlingApiException
    {
        TokenProvider tokenProvider = mock(TokenProvider.class);
        ProxyConfiguration proxyConfiguration = mock(ProxyConfiguration.class);
        projectApiClient = new ProjectApiClientImpl.Builder(PROJECT_ID)
                .authWithUserIdAndSecret("userId", "userSecret")
                .proxyConfiguration(proxyConfiguration)
                .withCustomTokenProvider(tokenProvider)
                .build();
        HttpUtils httpUtils = mock(HttpUtils.class);
        setField(projectApiClient, "httpUtils", httpUtils);
        response = mock(StringResponse.class);
        when(tokenProvider.getAuthenticationToken()).thenReturn(new AuthenticationToken("userSecret", "BEARER"));
        when(response.isSuccess()).thenReturn(true);
        when(httpUtils.executeHttpCall(requestCaptor.capture(), eq(proxyConfiguration))).thenReturn(response);
    }

    @Test
    public void testGetDetails() throws Exception
    {
        when(response.getContents()).thenReturn(ProjectResponseExamples.PROJECT_DETAILS_RESPONSE);
        ProjectDetails detailsResponse = projectApiClient.getDetails();
        HttpRequestBase request = requestCaptor.getValue();
        assertEquals(USER_TOKEN, request.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
        assertEquals(HttpGet.class, request.getClass());
        assertEquals("https://api.smartling.com/projects-api/v2/projects/" + PROJECT_ID, request.getURI().toString());
        assertEquals(PROJECT_ID, detailsResponse.getProjectId());
        assertEquals("Project 111", detailsResponse.getProjectName());
        assertEquals(false, detailsResponse.isArchived());
        assertEquals(new TargetLocale("de-DE", "German (Germany)"), detailsResponse.getTargetLocales().get(0));
    }


    @Test
    public void testGetDetailsBadResponse() throws Exception
    {
        when(this.response.getContents()).thenReturn(ProjectResponseExamples.PROJECT_DETAILS_BAD_RESPONSE);

        expectedEx.expect(SmartlingApiException.class);
        expectedEx.expectMessage("Response hasn't been parsed correctly [response=");

        projectApiClient.getDetails();
    }
}