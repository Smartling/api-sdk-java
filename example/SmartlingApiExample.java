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
import com.smartling.api.sdk.file.*;
import com.smartling.api.sdk.file.response.*;
import java.io.File;
import org.apache.commons.io.FilenameUtils;

public class SmartlingApiExample
{
    private static final String   API_KEY       = "YOUR-API-KEY";
    private static final String   PROJECT_ID    = "YOUR-PROJECT-ID";
    private static final String   LOCALE        = "YOUR-LOCALE";

    private static final String   PATH_TO_FILE  = "resources/test.properties";
    private static final String   FILE_ENCODING = "UTF-8";
    private static final FileType FILE_TYPE     = FileType.JAVA_PROPERTIES;
    private static final String   CALLBACK_URL  = null;

    public static void main(String args[]) throws FileApiException
    {
        FileApiClientAdapter smartlingFAPI = new FileApiClientAdapterImpl(true, API_KEY, PROJECT_ID);

        // upload the file
        File file = new File(FilenameUtils.separatorsToSystem(PATH_TO_FILE));
        ApiResponse<UploadData> uploadFileResponse = smartlingFAPI.uploadFile(FILE_TYPE, getFileUri(file), file, false, FILE_ENCODING, CALLBACK_URL);
        System.out.println(uploadFileResponse);

        // get last modified date
        ApiResponse<FileLastModified> lastModifiedResponse = smartlingFAPI.getLastModified(getFileUri(file), null, LOCALE);
        System.out.println(lastModifiedResponse);

        // rename the file
        final String fileIdentifier = "myTestFileIdentifier";
        ApiResponse<EmptyResponse> renameFileResponse = smartlingFAPI.renameFile(getFileUri(file), fileIdentifier);
        System.out.println(renameFileResponse);

        // run a search for files
        FileListSearchParams fileListSearchParams = new FileListSearchParams();
        fileListSearchParams.setUriMask(fileIdentifier);
        ApiResponse<FileList> filesListResponse = smartlingFAPI.getFilesList(fileListSearchParams);
        System.out.println(filesListResponse);

        // check the file status
        ApiResponse<FileStatus> fileStatusResponse = smartlingFAPI.getFileStatus(fileIdentifier, LOCALE);
        System.out.println(fileStatusResponse);

        // get the file back, including any translations that have been published.
        StringResponse translatedContent = smartlingFAPI.getFile(fileIdentifier, LOCALE, RetrievalType.PUBLISHED);
        System.out.println(translatedContent.getContents());

        // delete the file
        ApiResponse<EmptyResponse> deleteFileResponse = smartlingFAPI.deleteFile(fileIdentifier);
        System.out.println(deleteFileResponse);
    }

    private static String getFileUri(File file)
    {
        return file.getName();
    }
}
