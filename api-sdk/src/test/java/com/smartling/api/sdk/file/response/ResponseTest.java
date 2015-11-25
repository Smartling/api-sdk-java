package com.smartling.api.sdk.file.response;

import com.smartling.api.sdk.exceptions.SmartlingApiException;
import com.smartling.web.api.v2.ResponseCode;
import com.smartling.web.api.v2.Error;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.assertNotNull;

public class ResponseTest
{

    @Test
    public void testRetrieveData() throws Exception
    {
        Response<FileStatus> response = new Response<>();
        response.setData(new FileStatus());
        response.setCode(ResponseCode.SUCCESS);
        assertNotNull(response.retrieveData());
    }

    @Test(expected=SmartlingApiException.class)
    public void testRetrieveDataFromFailedResponse() throws Exception
    {
        Response<FileStatus> response = new Response<>();
        final LinkedList<Error> errors = new LinkedList<Error>();
        errors.add(new Error("VALIDATION_ERROR","AAAAA WE ALL GONNA DIE!!!", new ErrorDetails()));
        response.setErrors(errors);
        response.setCode(ResponseCode.VALIDATION_ERROR);
        response.retrieveData();
    }
}