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
import java.util.ArrayList;

import com.smartling.api.sdk.file.response.ApiResponse;
import com.smartling.api.sdk.file.response.FileList;
import com.smartling.api.sdk.file.response.FileStatus;
import com.smartling.api.sdk.file.response.UploadData;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

public class FileApiClientAdapterTest
{
    private static final String  TEST_FILE_ENCODING     = "UTF-8";
    private FileApiClientAdapter fileApiClientAdapter;

    private String               locale;

    @Before
    public void setup()
    {
        String apiKey = FileApiTestHelper.getApiKey();
        String projectId = FileApiTestHelper.getProjectId();
        locale = FileApiTestHelper.getLocale();

        fileApiClientAdapter = new FileApiClientAdapterImpl(FileApiTestHelper.getApiHost(), apiKey, projectId);
    }

    @Test(expected = Exception.class)
    public void testInvalidConstructor()
    {
        fileApiClientAdapter = new FileApiClientAdapterImpl(null, null, null);
    }

    @Test
    public void testUploadFile() throws FileApiException
    {
        File fileForUpload = FileApiTestHelper.getTestFile();
        ApiResponse<UploadData> uploadFileResponse = uploadFile(fileForUpload);

        assertEquals(FileApiTestHelper.STRING_COUNT_FROM_FILE, uploadFileResponse.getData().getStringCount());
        assertEquals(FileApiTestHelper.WORD_COUNT_FROM_FILE, uploadFileResponse.getData().getWordCount());
    }

    @Test
    public void testUploadFileThenDownloadOriginal() throws FileApiException, IOException
    {
        File fileForUpload = FileApiTestHelper.getTestFile();
        uploadFile(fileForUpload);

        // Null locale returns the original content of the file
        String fileContents = fileApiClientAdapter.getFile(getFileUri(fileForUpload), null);
        assertEquals(FileUtils.readFileToString(fileForUpload), fileContents);
    }

    @Test
    public void testUploadFileThenCheckListingOfFile() throws FileApiException
    {
        File fileForUpload = FileApiTestHelper.getTestFile();
        uploadFile(fileForUpload);

        FileListSearchParams fileListSearchParams = buildFileListSearchParams();
        ApiResponse<FileList> fileList = fileApiClientAdapter.getFilesList(fileListSearchParams);
        assertEquals(1, fileList.getData().getFileCount());
        verifyFileStatus(fileForUpload, fileList.getData().getFileList().get(0));
    }

    private FileListSearchParams buildFileListSearchParams()
    {
        FileListSearchParams fileListSearchParams = new FileListSearchParams();
        List<String> fileTypes = new ArrayList<String>();
        fileTypes.add(FileApiTestHelper.getTestFileType());
        fileListSearchParams.setFileTypes(fileTypes);
        fileListSearchParams.setUriMask(getFileUri(FileApiTestHelper.getTestFile()));

        return fileListSearchParams;
    }

    @Test
    public void testUploadFileThenCheckStatus() throws FileApiException
    {
        File fileForUpload = FileApiTestHelper.getTestFile();
        uploadFile(fileForUpload);

        ApiResponse<FileStatus> fileStatus = fileApiClientAdapter.getFileStatus(getFileUri(fileForUpload), locale);
        verifyFileStatus(fileForUpload, fileStatus.getData());
    }

    private void verifyFileStatus(File fileForUpload, FileStatus fileStatus)
    {
        assertEquals(getFileUri(fileForUpload), fileStatus.getFileUri());
        assertEquals(FileApiTestHelper.STRING_COUNT_FROM_FILE, fileStatus.getStringCount());
        assertEquals(FileApiTestHelper.WORD_COUNT_FROM_FILE, fileStatus.getWordCount());
    }

    private String getFileUri(File fileForUpload)
    {
        return fileForUpload.getName();
    }

    private ApiResponse<UploadData> uploadFile(File fileForUpload) throws FileApiException
    {
        return fileApiClientAdapter.uploadFile(FileApiTestHelper.getTestFileType(), getFileUri(fileForUpload), fileForUpload.getAbsolutePath(), null, TEST_FILE_ENCODING);
    }
}
