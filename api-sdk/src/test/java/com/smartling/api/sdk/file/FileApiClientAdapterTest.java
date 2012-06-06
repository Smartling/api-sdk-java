/* Copyright 2012 Smartling, Inc.
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
 * limitations under the License. */
package com.smartling.api.sdk.file;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.util.List;

import com.smartling.api.sdk.file.response.ApiResponse;
import com.smartling.api.sdk.file.response.EmptyResponse;
import com.smartling.api.sdk.file.response.FileList;
import com.smartling.api.sdk.file.response.FileStatus;
import com.smartling.api.sdk.file.response.StringResponse;
import com.smartling.api.sdk.file.response.UploadData;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

public class FileApiClientAdapterTest
{
    private static final String  SKD_FILE_URI       = "sdk-test-file-%s";
    private static final String  TEST_FILE_ENCODING = "UTF-8";
    private FileApiClientAdapter fileApiClientAdapter;

    private String               locale;

    @Before
    public void setup()
    {
        boolean testMode = FileApiTestHelper.getTestMode();
        String apiKey = FileApiTestHelper.getApiKey();
        String projectId = FileApiTestHelper.getProjectId();
        locale = FileApiTestHelper.getLocale();

        fileApiClientAdapter = new FileApiClientAdapterImpl(testMode, apiKey, projectId);
    }

    @Test(expected = Exception.class)
    public void testInvalidConstructor()
    {
        fileApiClientAdapter = new FileApiClientAdapterImpl(null, null, null);
    }

    @Test
    public void testFileActions() throws FileApiException, IOException
    {
        // /file/upload
        String originalFileUri = createFileUri();
        File fileForUpload = FileApiTestHelper.getTestFile();
        ApiResponse<UploadData> uploadFileResponse = uploadFile(fileForUpload, originalFileUri);
        FileApiTestHelper.validateSuccessUpload(uploadFileResponse);

        // /file/get
        StringResponse fileContents = fileApiClientAdapter.getFile(originalFileUri, null, null);
        assertEquals(FileUtils.readFileToString(fileForUpload), fileContents.getContents());

        // /file/rename
        String fileUri = createFileUri();
        fileApiClientAdapter.renameFile(originalFileUri, fileUri);

        // /file/list
        FileListSearchParams fileListSearchParams = new FileListSearchParams();
        ApiResponse<FileList> fileList = fileApiClientAdapter.getFilesList(fileListSearchParams);
        verifyFileListHasFileUri(fileUri, fileList.getData().getFileList());

        // /file/status
        ApiResponse<FileStatus> fileStatus = fileApiClientAdapter.getFileStatus(fileUri, locale);
        verifyFileStatus(fileUri, fileStatus.getData());

        // file/delete
        ApiResponse<EmptyResponse> deleteFileResponse = fileApiClientAdapter.deleteFile(fileUri);

        try
        {
            fileApiClientAdapter.getFile(fileUri, null, null);
            fail("File has been deleted. File should not be returned");
        }
        catch (FileApiException e)
        {
        }
    }

    private void verifyFileListHasFileUri(String fileUri, List<FileStatus> fileList)
    {
        boolean foundMatchingFileUri = false;
        for(FileStatus fileStatus : fileList)
        {
            if (fileStatus.getFileUri().equals(fileUri))
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

    private void verifyFileStatus(String expectedFileUri, FileStatus fileStatus)
    {
        assertEquals(expectedFileUri, fileStatus.getFileUri());
    }

    private ApiResponse<UploadData> uploadFile(File fileForUpload, String fileUri) throws FileApiException
    {
        return fileApiClientAdapter.uploadFile(FileApiTestHelper.getTestFileType(), fileUri, fileForUpload, null, TEST_FILE_ENCODING);
    }
}
