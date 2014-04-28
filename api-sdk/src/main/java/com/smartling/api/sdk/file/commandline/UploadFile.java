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

import com.smartling.api.sdk.FileApiClientAdapterImpl;
import com.smartling.api.sdk.FileApiClientAdapter;
import com.smartling.api.sdk.exceptions.ApiException;
import com.smartling.api.sdk.file.FileType;
import com.smartling.api.sdk.file.parameters.FileUploadParameterBuilder;
import com.smartling.api.sdk.dto.ApiResponse;
import com.smartling.api.sdk.dto.file.UploadFileData;
import java.io.File;

import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides command line access for uploading a file from the Smartling Translation API.
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
     *
     * Optional arguments:
     * 1) callback url. Can be null.
     * </pre>
     * @throws ApiException if an exception occurs in the course of uploading the specified file.
     */
    public static void main(String[] args) throws ApiException
    {
        upload(args);
    }

    protected static ApiResponse<UploadFileData> upload(String[] args) throws ApiException
    {
        UploadFileParams uploadParams = getParameters(args);

        File file = new File(uploadParams.getPathToFile());
        FileApiClientAdapter smartlingFAPI = new FileApiClientAdapterImpl(uploadParams.isProductionMode(), uploadParams.getApiKey(), uploadParams.getProjectId());

        FileUploadParameterBuilder fileUploadParameterBuilder = new FileUploadParameterBuilder();
        fileUploadParameterBuilder
                .fileType(FileType.lookup(uploadParams.getFileType()))
                .fileUri(file.getName())
                .approveContent(uploadParams.getApproveContent())
                .callbackUrl(uploadParams.getCallbackUrl());

        ApiResponse<UploadFileData> uploadResponse = smartlingFAPI.uploadFile(file,
                CharEncoding.UTF_8, fileUploadParameterBuilder
        );

        logger.info(String.format(RESULT, file.getName(), uploadResponse));
        return uploadResponse;
    }

    private static UploadFileParams getParameters(String[] args)
    {
        Validate.isTrue(args.length >= 6, "Invalid number of arguments");
        UploadFileParams uploadParams = new UploadFileParams();
        uploadParams.setProductionMode(Boolean.valueOf(args[0]));
        uploadParams.setApiKey(args[1]);
        uploadParams.setProjectId(args[2]);
        uploadParams.setPathToFile(args[3]);
        uploadParams.setFileType(args[4]);
        uploadParams.setApproveContent(null == args[5] ? null : Boolean.valueOf(args[5]));

        if(args.length == 7)
            uploadParams.setCallbackUrl(args[6]);

        return uploadParams;
    }
}
