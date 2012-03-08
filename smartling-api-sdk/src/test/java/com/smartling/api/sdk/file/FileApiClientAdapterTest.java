package com.smartling.api.sdk.file;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

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

        ApiResponse<FileList> fileList = fileApiClientAdapter.getFilesList(null);
        assertTrue(fileList.getData().getFileCount() >= 1);
        verifyContainsFileUri(fileList.getData().getFileList(), getFileUri(fileForUpload));
    }

    private void verifyContainsFileUri(List<FileStatus> fileStatuses, String fileUri)
    {
        for (FileStatus fileStatus : fileStatuses)
            if (fileUri.equals(fileStatus.getFileUri()))
                return;

        fail("The file could not be found in the list");
    }

    @Test
    public void testUploadFileThenCheckStatus() throws FileApiException
    {
        File fileForUpload = FileApiTestHelper.getTestFile();
        uploadFile(fileForUpload);

        ApiResponse<FileStatus> fileStatus = fileApiClientAdapter.getFileStatus(getFileUri(fileForUpload), locale);
        assertEquals(fileStatus.getData().getFileUri(), getFileUri(fileForUpload));
    }

    private String getFileUri(File fileForUpload)
    {
        return fileForUpload.getName();
    }

    private ApiResponse<UploadData> uploadFile(File fileForUpload) throws FileApiException
    {
        return fileApiClientAdapter.uploadFile(FileApiTestHelper.getTestFileType(), getFileUri(fileForUpload), fileForUpload.getAbsolutePath(), TEST_FILE_ENCODING);
    }
}
