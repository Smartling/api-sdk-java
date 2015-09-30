package com.smartling.api.sdk.exceptions;

import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ApiExceptionTest
{

    public static final String ERROR_RESPONSE = "{\"response\":{\"data\":null,\"code\":\"VALIDATION_ERROR\",\"messages\":[\"apiKey parameter is required\",\"apiVersion parameter is required\"]}}";

    @Test
    public void shouldReturnExceptionUsingContentsAndHttpCode()
    {
        ApiException apiException = ApiException.newException(ERROR_RESPONSE, 0);

        assertThat(apiException, instanceOf(ApiException.class));
    }

    @Test
    public void shouldRetrieveCode()
    {
        ApiException apiException = ApiException.newException(ERROR_RESPONSE, 0);

        assertThat(apiException.getApiCode(), is(equalTo("VALIDATION_ERROR")));
    }

    @Test
    public void shouldRetrieveMessages()
    {
        ApiException apiException = ApiException.newException(ERROR_RESPONSE, 0);

        assertThat(apiException.getMessages(), hasItems(equalTo("apiKey parameter is required"), equalTo("apiVersion parameter is required")));
    }

    @Test
    public void shouldPassHttpCode()
    {
        ApiException apiException = ApiException.newException(ERROR_RESPONSE, 123);

        assertThat(apiException.getHttpCode(), is(123));
    }

    @Test
    public void shouldSetNetworkErrorCodeInCaseIoException() {
        ApiException apiException = ApiException.newException(new IOException("Some exception"));

        assertThat(apiException.getApiCode(), is("GENERAL_ERROR"));
    }
}