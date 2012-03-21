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
import com.smartling.api.sdk.file.response.ApiResponse;
import com.smartling.api.sdk.file.response.UploadData;
import java.io.File;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

/**
 * Provides command line access for uploading a file from the Smartling API.
 */
public class UploadFile
{
    private static final Log    logger = LogFactory.getLog("com.smartling.api.sdk.file.commandline.UploadFile");

    private static final String RESULT = "Result for %s: %s";

    /**
     * @param args The arguments to pass in.
     * <pre>
     * 6 Arguments are expected:
     * 1) boolean if production should be used (true), or sandbox (false)
     * 2) apiKey
     * 3) projectId
     * 4) pathToPropertyFile
     * 5) type of file.
     * 6) Whether the contents of the file should be approved by default. Can be null, null values uses server default of false.
     * </pre>
     * @throws Exception if an exception occurs in the course of uploading the specified file.
     */
    public static void main(String[] args) throws Exception
    {
        upload(args);
    }

    protected static ApiResponse<UploadData> upload(String[] args) throws FileApiException
    {
        UploadFileParams uploadParams = getParameters(args);

        File file = new File(uploadParams.getPathToFile());
        FileApiClientAdapter smartlingFAPI = new FileApiClientAdapterImpl(uploadParams.isProductionMode(), uploadParams.getApiKey(), uploadParams.getProjectId());
        ApiResponse<UploadData> uploadResponse = smartlingFAPI.uploadFile(uploadParams.getFileType(), file.getName(), file, uploadParams.getApproveContent(), FileApiClientAdapterImpl.DEFAULT_ENCODING);

        logger.info(String.format(RESULT, file.getName(), uploadResponse));
        return uploadResponse;
    }

    private static UploadFileParams getParameters(String[] args)
    {
        Assert.isTrue(args.length == 6, "Invalid number of arguments");
        UploadFileParams uploadParams = new UploadFileParams();
        uploadParams.setProductionMode(Boolean.valueOf(args[0]));
        uploadParams.setApiKey(args[1]);
        uploadParams.setProjectId(args[2]);
        uploadParams.setPathToFile(args[3]);
        uploadParams.setFileType(args[4]);
        uploadParams.setApproveContent(null == args[5] ? null : Boolean.valueOf(args[5]));

        return uploadParams;
    }
}
