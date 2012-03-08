package com.smartling.api.sdk.file.commandline;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import com.smartling.api.sdk.file.FileApiTestHelper;
import com.smartling.api.sdk.file.response.ApiResponse;
import com.smartling.api.sdk.file.response.UploadData;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class UploadFileTest
{
    @Test
    public void testUploadFile() throws Exception
    {
        List<String> argList = buildFileUploadArgs();
        String[] args = new String[] {};
        ApiResponse<UploadData> uploadFileResponse = UploadFile.upload(argList.toArray(args));
        assertNotNull(uploadFileResponse);
        assertEquals(FileApiTestHelper.STRING_COUNT_FROM_FILE, uploadFileResponse.getData().getStringCount());
        assertEquals(FileApiTestHelper.WORD_COUNT_FROM_FILE, uploadFileResponse.getData().getWordCount());
    }

    @Test(expected = Exception.class)
    public void testInvalidNumberOfArguments() throws Exception
    {
        UploadFile.upload(new String[] {});
    }

    private List<String> buildFileUploadArgs()
    {
        List<String> args = new ArrayList<String>();
        args.add(FileApiTestHelper.getApiHost());
        args.add(FileApiTestHelper.getApiKey());
        args.add(FileApiTestHelper.getProjectId());
        args.add(FileApiTestHelper.getTestFile().getAbsolutePath());
        args.add(FileApiTestHelper.getTestFileType());

        return args;
    }

}
