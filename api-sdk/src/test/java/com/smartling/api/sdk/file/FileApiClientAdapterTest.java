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
package com.smartling.api.sdk.file;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import com.smartling.api.sdk.file.parameters.FileUploadParameterBuilder;
import com.smartling.api.sdk.file.response.Data;
import com.smartling.api.sdk.file.response.FileLastModified;
import java.util.HashMap;
import java.util.List;

import com.smartling.api.sdk.file.response.ApiResponse;
import com.smartling.api.sdk.file.response.EmptyResponse;
import com.smartling.api.sdk.file.response.FileList;
import com.smartling.api.sdk.file.response.FileStatus;
import com.smartling.api.sdk.file.response.StringResponse;
import com.smartling.api.sdk.file.response.UploadData;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

public class FileApiClientAdapterTest
{
    private static final Boolean APPROVE_CONTENT    = null;
    private static final String  SKD_FILE_URI       = "sdk-test-file-%s";
    private static final String  TEST_FILE_ENCODING = "UTF-8";
    private static final String  CALLBACK_URL       = "http://site.com/callback";
    private static final String  SUCCESS            = "SUCCESS";

    private FileApiClientAdapter fileApiClientAdapter;

    private String locale;

    @Before
    public void setup()
    {
        boolean testMode = FileApiTestHelper.getTestMode();
        String apiKey = FileApiTestHelper.getApiKey();
        String projectId = FileApiTestHelper.getProjectId();
        locale = FileApiTestHelper.getLocale();

        fileApiClientAdapter = new FileApiClientAdapterImpl(testMode, apiKey, projectId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidConstructor()
    {
        fileApiClientAdapter = new FileApiClientAdapterImpl(null, null);
    }

    @Test
    public void testFileActions() throws FileApiException, IOException
    {
        // /file/upload
        String originalFileUri = createFileUri();
        File fileForUpload = FileApiTestHelper.getTestFile();
        FileUploadParameterBuilder fileUploadParameterBuilder = new FileUploadParameterBuilder();
        fileUploadParameterBuilder
                .fileType(FileApiTestHelper.getTestFileType())
                .fileUri(originalFileUri)
                .approveContent(APPROVE_CONTENT)
                .callbackUrl(CALLBACK_URL);

        ApiResponse<UploadData> uploadFileResponse = fileApiClientAdapter.uploadFile(fileForUpload, TEST_FILE_ENCODING,
                fileUploadParameterBuilder);
        verifyApiResponse(uploadFileResponse);
        FileApiTestHelper.validateSuccessUpload(uploadFileResponse);

        // /file/upload (with directives)
        String originalFileUri2 = createFileUri();
        File fileForUpload2 = FileApiTestHelper.getTestFile();

        Map<String, String> smartlingDirectives = new HashMap<String, String>();

        smartlingDirectives.put("smartling.placeholder_format", "JAVA");
        smartlingDirectives.put("smartling.instruction_attributes", "comment, note");

        FileUploadParameterBuilder fileUploadParameterBuilderExtended = new FileUploadParameterBuilder();
        fileUploadParameterBuilderExtended.fileType(FileApiTestHelper.getTestFileType())
                .fileUri(originalFileUri2)
                .approveContent(APPROVE_CONTENT)
                .callbackUrl(CALLBACK_URL)
                .localesToApprove(null)
                .overwriteApprovedLocales(null)
                .directives(smartlingDirectives);

        ApiResponse<UploadData> uploadFileResponse2 = fileApiClientAdapter.uploadFile(fileForUpload2,
                TEST_FILE_ENCODING, fileUploadParameterBuilderExtended
        );

        verifyApiResponse(uploadFileResponse2);
        FileApiTestHelper.validateSuccessUpload(uploadFileResponse2);

        // /file/last_modified
        ApiResponse<FileLastModified> lastModifiedResponse = fileApiClientAdapter.getLastModified(originalFileUri, null, locale);
        verifyApiResponse(lastModifiedResponse);
        verifyFileLastModified(lastModifiedResponse.getData());

        // /file/get
        StringResponse fileContents = fileApiClientAdapter.getFile(originalFileUri, null, null);
        assertEquals(FileUtils.readFileToString(fileForUpload), fileContents.getContents());

        // /file/rename
        String fileUri = createFileUri();
        ApiResponse<EmptyResponse> renameFileResponse = fileApiClientAdapter.renameFile(originalFileUri, fileUri);
        verifyApiResponse(renameFileResponse);

        // /file/list
        FileListSearchParams fileListSearchParams = new FileListSearchParams();
        ApiResponse<FileList> fileListResponse = fileApiClientAdapter.getFilesList(fileListSearchParams);
        verifyApiResponse(fileListResponse);
        verifyFileListHasFileUri(fileUri, CALLBACK_URL, fileListResponse.getData().getFileList());

        // /file/status
        ApiResponse<FileStatus> fileStatusResponse = fileApiClientAdapter.getFileStatus(fileUri, locale);
        verifyApiResponse(fileStatusResponse);
        verifyFileStatus(fileUri, CALLBACK_URL, fileStatusResponse.getData());

        // file/delete
        ApiResponse<EmptyResponse> deleteFileResponse = fileApiClientAdapter.deleteFile(fileUri);
        verifyApiResponse(deleteFileResponse);
    }

    private void verifyFileListHasFileUri(String fileUri, String callbackUrl, List<FileStatus> fileList)
    {
        boolean foundMatchingFileUri = false;
        for (FileStatus fileStatus : fileList)
        {
            if (fileStatus.getFileUri().equals(fileUri) && fileStatus.getCallbackUrl().equals(callbackUrl))
            {
                foundMatchingFileUri = true;
                break;
            }
        }
        assertTrue("File Uri not found", foundMatchingFileUri);
    }

    private String createFileUri()
    {
        return String.format(SKD_FILE_URI, System.currentTimeMillis());
    }

    private void verifyApiResponse(ApiResponse<? extends Data> apiResponse)
    {
        assertEquals(SUCCESS, apiResponse.getCode());
        assertEquals(0, apiResponse.getMessages().size());
    }

    private void verifyFileStatus(String expectedFileUri, String expectedCallbackUrl, FileStatus fileStatus)
    {
        assertEquals(expectedFileUri, fileStatus.getFileUri());
        assertEquals(expectedCallbackUrl, fileStatus.getCallbackUrl());
    }

    private void verifyFileLastModified(FileLastModified lastModified)
    {
        assertNotNull(lastModified.getItems());
        assertEquals(1, lastModified.getItems().size());
        assertEquals(locale, lastModified.getItems().get(0).getLocale());
        assertNotNull(lastModified.getItems().get(0).getLastModified());
    }
}
