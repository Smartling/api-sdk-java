/*
 * Copyright 2012 Smartling, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this work except in compliance with the License.
 * You may obtain a copy of the License in the LICENSE file, or at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.smartling.api.sdk.file.commandline;

import com.smartling.api.sdk.file.FileApiClientAdapter;
import com.smartling.api.sdk.file.FileApiClientAdapterImpl;
import com.smartling.api.sdk.file.FileApiException;
import com.smartling.api.sdk.file.response.StringResponse;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

/**
 * Provides command line access for retrieving a file from the Smartling Translation API.
 */
public class RetrieveFile
{
    private static final Log    logger                    = LogFactory.getLog("com.smartling.api.sdk.file.commandline.RetrieveFile");

    private static final String LOCALE_SEPERATOR          = "-";
    private static final String LOCALE_FILENAME_SEPERATOR = "_";
    private static final String PROPERTY_FILE_EXT         = ".properties";
    private static final String RESULT                    = "New file created: %s";

    /**
     * @param args The arguments to pass in.
     * <pre>
     * 6 Arguments are expected:
     * 1) boolean if production should be used (true), or sandbox (false)
     * 2) apiKey
     * 3) projectId
     * 4) path to the property file to download (used to look up the fileUri).
     * 5) the locale to download the file for. Can be null if the original file is desired.
     * 6) path to store the file.
     * <pre>
     * @throws Exception if an exception occurs in the course of downloading the specified file.
     */
    public static void main(String[] args) throws Exception
    {
        File translatedFile = retrieve(args);

        logger.info(String.format(RESULT, translatedFile.getName()));
    }

    protected static File retrieve(String[] args) throws FileApiException, IOException
    {
        RetrieveFileParams retrieveFileParams = getParameters(args);

        File file = new File(retrieveFileParams.getPathToFile());
        FileApiClientAdapter smartlingFAPI = new FileApiClientAdapterImpl(retrieveFileParams.isProductionMode(), retrieveFileParams.getApiKey(), retrieveFileParams.getProjectId());
        StringResponse response = smartlingFAPI.getFile(file.getName(), retrieveFileParams.getLocale());

        File translatedFile = new File(getTranslatedFilePath(file, retrieveFileParams.getLocale(), retrieveFileParams.getPathToStoreFile()));
        FileUtils.writeStringToFile(translatedFile, response.getContents(), response.getEncoding());
        return translatedFile;
    }

    private static RetrieveFileParams getParameters(String[] args)
    {
        Assert.isTrue(args.length == 6, "Invalid number of arguments");

        RetrieveFileParams retrieveFileParams = new RetrieveFileParams();
        retrieveFileParams.setProductionMode(Boolean.valueOf(args[0]));
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
        String locale = StringUtils.isNotBlank(localeString) ? localeString.replace(LOCALE_SEPERATOR, LOCALE_FILENAME_SEPERATOR) : StringUtils.EMPTY;
        stringBuilder.append(file.getName().replace(PROPERTY_FILE_EXT, LOCALE_FILENAME_SEPERATOR + locale + PROPERTY_FILE_EXT));

        return stringBuilder.toString();
    }

}
