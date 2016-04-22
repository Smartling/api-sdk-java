package com.smartling.api.sdk.file.parameters;

import com.smartling.api.sdk.file.FileType;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class FileUploadParameterBuilderTest
{
    private FileUploadParameterBuilder testedInstance;

    @Before
    public void init(){
        testedInstance = new FileUploadParameterBuilder(FileType.JSON, "fileUri");
    }

    @Test
    public void shouldContainDefaultClientLibIdDirectiveIfNonIsProvided()
    {
        String expectedValue = "{\"client\":\"test-artifact-id\",\"version\":\"1.0.0\"}";

        List<NameValuePair> nameValueList = testedInstance.getNameValueList();
        assertTrue(nameValueList.contains(new BasicNameValuePair(FileApiParameter.CLIENT_LIB_ID, expectedValue)));
    }

}