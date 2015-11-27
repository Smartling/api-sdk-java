package com.smartling.api.sdk.file.response;

import com.smartling.api.sdk.exceptions.SmartlingApiException;
import com.smartling.web.api.v2.ResponseCode;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.LinkedList;

import static org.junit.Assert.assertNotNull;

public class ResponseTest
{

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();


    @Test
    public void testRetrieveData() throws Exception
    {
        Response<FileStatus> response = new Response<>();
        response.setData(new FileStatus());
        response.setCode(ResponseCode.SUCCESS);
        assertNotNull(response.retrieveData());
    }

    @Test
    public void testRetrieveDataFromFailedResponse() throws Exception
    {
        expectedEx.expect(SmartlingApiException.class);
        expectedEx.expectMessage("VALIDATION_ERROR\n"
                        + "Error{key='VALIDATION_ERROR', message='AAAAA WE ALL GONNA DIE!!!', details=ErrorDetails{field='null', errorId='null'}}");

        Response<FileStatus> response = new Response<>();
        final LinkedList<Error> errors = new LinkedList<Error>();
        errors.add(new Error("VALIDATION_ERROR","AAAAA WE ALL GONNA DIE!!!", new ErrorDetails()));
        response.setErrors(errors);
        response.setCode(ResponseCode.VALIDATION_ERROR);
        response.retrieveData();

    }
}