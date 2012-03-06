package com.smartling.api.sdk.file.commandline;

import com.smartling.api.sdk.file.FileApiClientAdapter;
import com.smartling.api.sdk.file.FileApiClientAdapterImpl;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

public class RetrieveFile
{
    private static final Log    logger                    = LogFactory.getLog("com.smartling.api.sdk.file.commandline.RetrieveFile");

    private static final String LOCALE_SEPERATOR          = "-";
    private static final String LOCALE_FILENAME_SEPERATOR = "_";
    private static final String PROPERTY_FILE_EXT         = ".properties";
    private static final String RESULT                    = "New file created: %s";

    /**
     * @param args The arguments to pass in.
     * 6 Arguments are expected:
     * 1) baseApiUrl
     * 2) apiKey
     * 3) projectId
     * 4) path to the property file to download (used to look up the fileUri).
     * 5) the locale to download the file for.
     * 6) path to store the file.
     *
     * @throws Exception if an exception occurs in the course of downloading the specified file.
     */
    public static void main(String[] args) throws Exception
    {
        RetrieveFileParams retrieveFileParams = getParameters(args);

        File file = new File(retrieveFileParams.getPathToFile());
        FileApiClientAdapter smartlingFAPI = new FileApiClientAdapterImpl(retrieveFileParams.getBaseApiUrl(), retrieveFileParams.getApiKey(), retrieveFileParams.getProjectId());
        String result = smartlingFAPI.getFile(file.getName(), retrieveFileParams.getLocale());

        File translatedFile = new File(getTranslatedFilePath(file, retrieveFileParams.getLocale(), retrieveFileParams.getPathToStoreFile()));
        FileUtils.writeStringToFile(translatedFile, result, "UTF-8");

        logger.info(String.format(RESULT, translatedFile.getName()));
    }

    private static RetrieveFileParams getParameters(String[] args)
    {
        Assert.isTrue(args.length == 6, "Invalid number of arguments");

        RetrieveFileParams retrieveFileParams = new RetrieveFileParams();
        retrieveFileParams.setBaseApiUrl(args[0]);
        retrieveFileParams.setApiKey(args[1]);
        retrieveFileParams.setProjectId(args[2]);
        retrieveFileParams.setPathToFile(args[3]);
        retrieveFileParams.setLocale(args[4]);
        retrieveFileParams.setPathToStoreFile(args[5]);

        return retrieveFileParams;
    }

    private static String getTranslatedFilePath(File file, String localeString, String pathToStoreFile)
    {
        StringBuilder stringBuilder = new StringBuilder(pathToStoreFile);
        stringBuilder.append(file.getName().replace(PROPERTY_FILE_EXT, LOCALE_FILENAME_SEPERATOR + localeString.replace(LOCALE_SEPERATOR, LOCALE_FILENAME_SEPERATOR) + PROPERTY_FILE_EXT));

        return stringBuilder.toString();
    }

}
