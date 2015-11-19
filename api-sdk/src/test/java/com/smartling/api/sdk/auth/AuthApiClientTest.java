package com.smartling.api.sdk.auth;

import com.smartling.api.sdk.ProxyConfiguration;
import com.smartling.api.sdk.dto.file.StringResponse;
import com.smartling.api.sdk.exceptions.ApiException;
import com.smartling.api.sdk.file.response.Response;
import com.smartling.api.sdk.util.HttpUtils;
import com.smartling.web.api.v2.ResponseCode;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthApiClientTest
{

    private HttpUtils httpUtils;
    private ArgumentCaptor<HttpRequestBase> requestCaptor = ArgumentCaptor.forClass(HttpRequestBase.class);
    private ProxyConfiguration proxyConfiguration;
    private StringResponse response;
    private AuthApiClient authApiClient;

    private static final String BASE_PATH = "https://auth.smartling.com";
    private static final String USER_ID = "USER_ID";
    private static final String USER_SECRET = "USER_SECRET";

    private static final String AUTH_RESPONSE = "{\n"
            + "  \"response\": {\n"
            + "    \"code\": \"SUCCESS\",\n"
            + "    \"data\": {\n"
            + "      \"accessToken\": \"eyJhbGciOiJSUzI1NiJ9"
            + ".eyJqdGkiOiI1MTFlODBlNy00ZTMxLTQwNWUtYWExMy0zZTY5YjQ4NmVmYzkiLCJleHAiOjE0NDc4NjYwMDksIm5iZiI6MCwiaWF0IjoxNDQ3ODY1NzA5LCJpc3MiOiJodHRwczovL3Nzby5zbWFydGxpbmcuY29tL2F1dGgvcmVhbG1zL1NtYXJ0bGluZyIsImF1ZCI6ImF1dGhlbnRpY2F0aW9uLXNlcnZpY2UiLCJzdWIiOiI5ZDY3ZmI5Yy00ZjIwLTQ1N2ItOTlmZC04MDg4OThjMzA0M2UiLCJhenAiOiJhdXRoZW50aWNhdGlvbi1zZXJ2aWNlIiwic2Vzc2lvbl9zdGF0ZSI6IjFkZDIwMjZiLTgyYzgtNGNlMy05NjUxLTY2NDZmOGY2MmJiZCIsImNsaWVudF9zZXNzaW9uIjoiMGQyYTQ4M2EtOTI3Ny00YWE2LTg1MzYtZWFkNDcyZDg2ZjQ3IiwiYWxsb3dlZC1vcmlnaW5zIjpbXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIlJPTEVfQVBJX1VTRVIiLCJ1c2VyIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJ2aWV3LXByb2ZpbGUiLCJtYW5hZ2UtYWNjb3VudCJdfX0sInVpZCI6IjhmZTFiYTEyM2UyMyIsImVtYWlsIjoic3Jvc3NpbGxvK2RtcythY2NvdW50K293bkBzbWFydGxpbmcuY29tIiwibmFtZSI6IlRlc3QgQWNjb3VudCBPd25lciBUb2tlbiIsInByZWZlcnJlZF91c2VybmFtZSI6ImFwaTpxUHdvZWVDS1NIQXdpUEdKdHdpZk5pTFhsTktXaU8ifQ.K0HT0wOqQZzWEVWWeo1Uy21BZvbBj2rg0Kbvvz1ZhQA1l4pgx1KdkX1UCUiaIAdlSCv7N-O8EJwTSpVtwH-xHjAVwZavhRnbCMRqJOF1gB98NxUUA7Qp5w4Vt22hFZ6kfM72UAmfPs46VT8L61oqoRKUe9oxTF0WOOFDr9zpWbs\",\n"
            + "      \"refreshToken\": \"eyJhbGciOiJSUzI1NiJ9"
            + ".eyJqdGkiOiIxODFlN2MzNi1jZTE2LTQ3ZTUtYWE3ZC04MjlmZmZkNDU4OTYiLCJleHAiOjE0NDc4NjkzNjksIm5iZiI6MCwiaWF0IjoxNDQ3ODY1NzA5LCJpc3MiOiJodHRwczovL3Nzby5zbWFydGxpbmcuY29tL2F1dGgvcmVhbG1zL1NtYXJ0bGluZyIsInN1YiI6IjlkNjdmYjljLTRmMjAtNDU3Yi05OWZkLTgwODg5OGMzMDQzZSIsInR5cCI6IlJFRlJFU0giLCJhenAiOiJhdXRoZW50aWNhdGlvbi1zZXJ2aWNlIiwic2Vzc2lvbl9zdGF0ZSI6IjFkZDIwMjZiLTgyYzgtNGNlMy05NjUxLTY2NDZmOGY2MmJiZCIsImNsaWVudF9zZXNzaW9uIjoiMGQyYTQ4M2EtOTI3Ny00YWE2LTg1MzYtZWFkNDcyZDg2ZjQ3IiwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIlJPTEVfQVBJX1VTRVIiLCJ1c2VyIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJ2aWV3LXByb2ZpbGUiLCJtYW5hZ2UtYWNjb3VudCJdfX19.Bjwzew-pwFB_8sG20OezmoCWMMqzZNjpQvMtd_yrNLJey9c3hqAGuXRM5yjtbKcHM5xkzvxOw4UBUIbVq6ElWVGsvrYXIYzEpCv7L_X2I9-cTJ9gC4kw8rWqXGWx2JYEh0WYsBs623lCB_2xjthP21sX94iwlOJVX3_3Huxw_48\",\n"
            + "      \"expiresIn\": 300,\n"
            + "      \"refreshExpiresIn\": 3660,\n"
            + "      \"tokenType\": \"Bearer\"\n"
            + "    }\n"
            + "  }\n"
            + "}";

    @Before
    public void setup() throws ApiException
    {
        authApiClient = new AuthApiClient();
        httpUtils = mock(HttpUtils.class);
        authApiClient.setHttpUtils(httpUtils);
        response = mock(StringResponse.class);
        proxyConfiguration = mock(ProxyConfiguration.class);
        when(httpUtils.executeHttpCall(requestCaptor.capture(), eq(proxyConfiguration))).thenReturn(response);
    }

    @Test
    public void testAuthenticate() throws Exception
    {
        when(response.getContents()).thenReturn(AUTH_RESPONSE);
        Response<AuthenticationContext> response = authApiClient.authenticate(new AuthenticationCommand(USER_ID, USER_SECRET), proxyConfiguration, BASE_PATH);
        HttpRequestBase request = requestCaptor.getValue();
        assertEquals(HttpPost.class, request.getClass());
        assertEquals("https://auth.smartling.com/auth-api/v2/authenticate", request.getURI().toString());
        assertEquals(ResponseCode.SUCCESS, response.getCode());
        assertEquals("eyJhbGciOiJSUzI1NiJ9.eyJqdGkiOiI1MTFlODBlNy00ZTMxLTQwNWUtYWExMy0zZTY5YjQ4NmVmYzkiLCJleHAiOjE0NDc4NjYwMDksIm5iZiI6MCwiaWF0IjoxNDQ3ODY1NzA5LCJpc3MiOiJodHRwczovL3Nzby5zbWFydGxpbmcuY29tL2F1dGgvcmVhbG1zL1NtYXJ0bGluZyIsImF1ZCI6ImF1dGhlbnRpY2F0aW9uLXNlcnZpY2UiLCJzdWIiOiI5ZDY3ZmI5Yy00ZjIwLTQ1N2ItOTlmZC04MDg4OThjMzA0M2UiLCJhenAiOiJhdXRoZW50aWNhdGlvbi1zZXJ2aWNlIiwic2Vzc2lvbl9zdGF0ZSI6IjFkZDIwMjZiLTgyYzgtNGNlMy05NjUxLTY2NDZmOGY2MmJiZCIsImNsaWVudF9zZXNzaW9uIjoiMGQyYTQ4M2EtOTI3Ny00YWE2LTg1MzYtZWFkNDcyZDg2ZjQ3IiwiYWxsb3dlZC1vcmlnaW5zIjpbXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIlJPTEVfQVBJX1VTRVIiLCJ1c2VyIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJ2aWV3LXByb2ZpbGUiLCJtYW5hZ2UtYWNjb3VudCJdfX0sInVpZCI6IjhmZTFiYTEyM2UyMyIsImVtYWlsIjoic3Jvc3NpbGxvK2RtcythY2NvdW50K293bkBzbWFydGxpbmcuY29tIiwibmFtZSI6IlRlc3QgQWNjb3VudCBPd25lciBUb2tlbiIsInByZWZlcnJlZF91c2VybmFtZSI6ImFwaTpxUHdvZWVDS1NIQXdpUEdKdHdpZk5pTFhsTktXaU8ifQ.K0HT0wOqQZzWEVWWeo1Uy21BZvbBj2rg0Kbvvz1ZhQA1l4pgx1KdkX1UCUiaIAdlSCv7N-O8EJwTSpVtwH-xHjAVwZavhRnbCMRqJOF1gB98NxUUA7Qp5w4Vt22hFZ6kfM72UAmfPs46VT8L61oqoRKUe9oxTF0WOOFDr9zpWbs", response.getData().getAccessToken());
        assertEquals(300, response.getData().getExpiresIn());
    }
}