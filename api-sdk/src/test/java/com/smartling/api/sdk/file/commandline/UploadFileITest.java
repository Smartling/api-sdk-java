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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.gson.internal.Pair;
import com.smartling.api.sdk.ApiTestHelper;
import com.smartling.api.sdk.dto.ApiResponse;
import com.smartling.api.sdk.dto.file.UploadFileData;
import com.smartling.api.sdk.exceptions.ApiException;
import com.smartling.api.sdk.file.FileType;

public class UploadFileITest
{
    private static final List<Pair<String, FileType>> FILES = new ArrayList<>();
    
    static
    {
        FILES.add(new Pair<>("resources/utf-16be.properties", ApiTestHelper.getTestFileType()));
        FILES.add(new Pair<>("resources/utf-16be-bom.properties", ApiTestHelper.getTestFileType()));
        FILES.add(new Pair<>("resources/utf-16le.properties", ApiTestHelper.getTestFileType()));
        FILES.add(new Pair<>("resources/utf-16le-bom.properties", ApiTestHelper.getTestFileType()));
        FILES.add(new Pair<>("resources/1250.properties", ApiTestHelper.getTestFileType()));
        FILES.add(new Pair<>("resources/sample.doc", FileType.DOC));
    }

    @Test
    public void testUploadFile() throws ApiException
    {
        List<String> argList = buildFileUploadArgs(ApiTestHelper.getTestFile().getAbsolutePath(), ApiTestHelper.getTestFileType());
        String[] args = new String[]{};
        ApiResponse<UploadFileData> uploadFileResponse = UploadFile.upload(argList.toArray(args));
        assertNotNull(uploadFileResponse);
        ApiTestHelper.verifyApiResponse(uploadFileResponse);
    }

    @Test
    public void testUploadDifferentFormatFiles() throws ApiException
    {
        for (Pair<String, FileType> file : FILES)
        {
            List<String> argList = buildFileUploadArgs(file.first, file.second);
            String[] args = new String[]{};
            ApiResponse<UploadFileData> uploadFileResponse = UploadFile.upload(argList.toArray(args));
            assertNotNull(uploadFileResponse);
            ApiTestHelper.verifyApiResponse(uploadFileResponse);
        }
    }


    @Test(expected = Exception.class)
    public void testInvalidNumberOfArguments() throws ApiException
    {
        UploadFile.upload(new String[]{});
    }

    private List<String> buildFileUploadArgs(String fileName, FileType fileType)
    {
        List<String> args = new ArrayList<String>();
        args.add(String.valueOf(ApiTestHelper.getTestMode()));
        args.add(ApiTestHelper.getApiKey());
        args.add(ApiTestHelper.getProjectId());
        args.add(fileName);
        args.add(fileType.getIdentifier());
        args.add(null);
        args.add(ApiTestHelper.getCallbackUrl());

        return args;
    }
}
