package com.smartling.api.sdk.file;

import java.io.File;
import junit.framework.Assert;
import org.apache.commons.io.FilenameUtils;

public class FileApiTestHelper
{
    public static final int     WORD_COUNT_FROM_FILE   = 10;
    public static final int     STRING_COUNT_FROM_FILE = 2;

    private static final String TEST_FILE_TYPE         = "javaProperties";
    public static final String  TEST_FILE_LOCATION     = "resources/test.properties";

    public static String getApiKey()
    {
        String apiKey = System.getProperty("apiKey");
        Assert.assertNotNull("The apiKey system property must be set", apiKey);

        return apiKey;
    }

    public static String getProjectId()
    {
        String projectId = System.getProperty("projectId");
        Assert.assertNotNull("The projectId system property must be set", projectId);

        return projectId;
    }

    public static String getLocale()
    {
        String locale = System.getProperty("locale");
        Assert.assertNotNull("The locale system property must be set", locale);

        return locale;
    }

    public static String getApiHost()
    {
        return "https://api.smartling.com/v1";
    }

    public static File getTestFile()
    {
        return new File(FilenameUtils.separatorsToSystem(FileApiTestHelper.TEST_FILE_LOCATION));
    }

    public static String getTestFileType()
    {
        return TEST_FILE_TYPE;
    }
}
