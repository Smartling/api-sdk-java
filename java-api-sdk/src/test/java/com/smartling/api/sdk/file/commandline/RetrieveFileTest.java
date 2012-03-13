package com.smartling.api.sdk.file.commandline;

import static junit.framework.Assert.assertNotNull;
import java.io.File;

import com.smartling.api.sdk.file.FileApiTestHelper;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class RetrieveFileTest
{
    @Test
    public void testRetrieveFile() throws Exception
    {
        List<String> argList = buildFileRetrieveArgs();
        String[] args = new String[] {};
        File file = RetrieveFile.retrieve(argList.toArray(args));
        assertNotNull(file);
    }

    @Test(expected = Exception.class)
    public void testInvalidNumberOfArguments() throws Exception
    {
        RetrieveFile.retrieve(new String[] {});
    }

    private List<String> buildFileRetrieveArgs()
    {
        List<String> args = new ArrayList<String>();
        args.add(FileApiTestHelper.getApiHost());
        args.add(FileApiTestHelper.getApiKey());
        args.add(FileApiTestHelper.getProjectId());
        args.add(FileApiTestHelper.getTestFile().getAbsolutePath());
        args.add(FileApiTestHelper.getLocale());
        args.add("bin/");
        args.add("UTF-8");

        return args;
    }
}
