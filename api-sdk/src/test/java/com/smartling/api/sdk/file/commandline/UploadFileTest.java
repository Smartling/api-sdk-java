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

import static junit.framework.Assert.assertNotNull;

import com.smartling.api.sdk.exceptions.FileApiException;
import com.smartling.api.sdk.ApiTestHelper;
import com.smartling.api.sdk.dto.ApiResponse;
import com.smartling.api.sdk.dto.file.UploadFileData;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class UploadFileTest
{
    @Test
    public void testUploadFile() throws FileApiException
    {
        List<String> argList = buildFileUploadArgs();
        String[] args = new String[]{};
        ApiResponse<UploadFileData> uploadFileResponse = UploadFile.upload(argList.toArray(args));
        assertNotNull(uploadFileResponse);
        ApiTestHelper.verifyApiResponse(uploadFileResponse);
    }

    @Test(expected = Exception.class)
    public void testInvalidNumberOfArguments() throws FileApiException
    {
        UploadFile.upload(new String[]{});
    }

    private List<String> buildFileUploadArgs()
    {
        List<String> args = new ArrayList<String>();
        args.add(String.valueOf(ApiTestHelper.getTestMode()));
        args.add(ApiTestHelper.getApiKey());
        args.add(ApiTestHelper.getProjectId());
        args.add(ApiTestHelper.getTestFile().getAbsolutePath());
        args.add(ApiTestHelper.getTestFileType().getIdentifier());
        args.add(null);
        args.add(ApiTestHelper.getCallbackUrl());

        return args;
    }
}
