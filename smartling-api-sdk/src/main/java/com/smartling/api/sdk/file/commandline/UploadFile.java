package com.smartling.api.sdk.file.commandline;

import com.smartling.api.sdk.file.FileApiClientAdapter;
import com.smartling.api.sdk.file.FileApiClientAdapterImpl;
import java.io.File;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

public class UploadFile
{
    private static final Log    logger                    = LogFactory.getLog("com.smartling.api.sdk.file.commandline.UploadFile");

    private static final String RESULT = "Result for %s: %s";

    /**
     * @param args The arguments to pass in.
     * 5 Arguments are expected:
     * 1) apiBaseUrl
     * 2) apiKey
     * 3) projectId
     * 4) pathToPropertyFile
     * 5) type of file.
     *
     * @throws Exception if an exception occurs in the course of uploading the specified file.
     */
    public static void main(String[] args) throws Exception
    {
        UploadFileParams uploadParams = getParameters(args);

        File file = new File(uploadParams.getPathToFile());
        FileApiClientAdapter smartlingFAPI = new FileApiClientAdapterImpl(uploadParams.getBaseApiUrl(), uploadParams.getApiKey(), uploadParams.getProjectId());
        String result = smartlingFAPI.uploadFile(uploadParams.getFileType(), file.getName(), uploadParams.getPathToFile(), FileApiClientAdapterImpl.DEFAULT_ENCODING);

        logger.info(String.format(RESULT, file.getName(), result));
    }

    private static UploadFileParams getParameters(String[] args)
    {
        Assert.isTrue(args.length == 5, "Invalid number of arguments");
        UploadFileParams uploadParams = new UploadFileParams();
        uploadParams.setBaseApiUrl(args[0]);
        uploadParams.setApiKey(args[1]);
        uploadParams.setProjectId(args[2]);
        uploadParams.setPathToFile(args[3]);
        uploadParams.setFileType(args[4]);

        return uploadParams;
    }
}
