package com.smartling.api.sdk.projects;

import com.smartling.api.sdk.ProxyConfiguration;
import com.smartling.api.sdk.auth.AuthenticationToken;
import com.smartling.api.sdk.auth.TokenProvider;
import com.smartling.api.sdk.dto.file.StringResponse;
import com.smartling.api.sdk.exceptions.SmartlingApiException;
import com.smartling.api.sdk.projects.response.ProjectDetails;
import com.smartling.api.sdk.util.HttpUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static mockit.Deencapsulation.setField;
import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectsApiClientImplTest
{
    private static final String PROJECT_ID = "testProject";

    private static final String EXPECTED_AUTHORIZATION_HEADER = "BEARER <token>";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ProjectsApiClient projectsApiClient;

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

        projectsApiClient = new ProjectsApiClientImpl.Builder(PROJECT_ID)
                .proxyConfiguration(proxyConfiguration)
                .withCustomTokenProvider(tokenProvider)
                .build();
        setField(projectsApiClient, "httpUtils", httpUtils);
    }

    @Test
    public void testGetProjectDetails() throws Exception
    {
        when(response.getContents()).thenReturn(ResponseExamples.PROJECT_DETAILS_RESPONSE);

        ProjectDetails apiResponse = projectsApiClient.getProjectDetails();

        HttpRequestBase request = requestCaptor.getValue();
        assertEquals(EXPECTED_AUTHORIZATION_HEADER, request.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
        assertEquals(HttpGet.class, request.getClass());
        assertEquals("https://api.smartling.com/projects-api/v2/projects/testProject", request.getURI().toString());
        assertEquals(PROJECT_ID, apiResponse.getProjectId());
        assertEquals("Project Name", apiResponse.getProjectName());
        assertEquals("test-account-uid", apiResponse.getAccountUid());
        assertFalse(apiResponse.isArchived());
        assertEquals("test-projects-type-code", apiResponse.getProjectTypeCode());
        assertEquals("test-source-locale-id", apiResponse.getSourceLocaleId());
        assertEquals("test-source-locale-description", apiResponse.getSourceLocaleDescription());

        assertEquals("be-BY", apiResponse.getTargetLocales().get(0).getLocaleId());
        assertEquals("test-locale-be-BY-description", apiResponse.getTargetLocales().get(0).getDescription());
        assertTrue(apiResponse.getTargetLocales().get(0).isEnabled());


        assertEquals("it-IT", apiResponse.getTargetLocales().get(1).getLocaleId());
        assertEquals("test-locale-it-IT-description", apiResponse.getTargetLocales().get(1).getDescription());
        assertFalse(apiResponse.getTargetLocales().get(1).isEnabled());
    }

    @Test
    public void testGetProjectDetailsWhenGetWrongResponse() throws Exception
    {
        when(response.getContents()).thenReturn(ResponseExamples.NOT_EXISTING_CODE_RESPONSE);

        expectedException.expect(SmartlingApiException.class);
        expectedException.expectMessage("Response hasn't been parsed correctly [response=");

        projectsApiClient.getProjectDetails();
    }

    @Test
    public void testGetProjectDetailsShouldThrowSmartlingApiExceptionWhenEmptyContent() throws Exception
    {
        when(response.getContents()).thenReturn("");

        expectedException.expect(SmartlingApiException.class);
        expectedException.expectMessage("Response hasn't been parsed correctly [response=");

        projectsApiClient.getProjectDetails();
    }

    @Test
    public void testGetProjectDetailsShouldThrowSmartlingApiExceptionWhenInvalidJson() throws Exception
    {
        when(response.getContents()).thenReturn("<b>This is not JSON</b>");

        expectedException.expect(SmartlingApiException.class);
        expectedException.expectMessage("Can't parse response as JSON [response=");

        projectsApiClient.getProjectDetails();
    }

}