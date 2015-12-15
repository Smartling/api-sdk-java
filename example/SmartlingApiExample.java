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

import com.smartling.api.sdk.dto.file.FileLastModified;
import com.smartling.api.sdk.dto.file.StringResponse;
import com.smartling.api.sdk.dto.file.UploadFileData;
import com.smartling.api.sdk.exceptions.SmartlingApiException;
import com.smartling.api.sdk.file.FileApiClient;
import com.smartling.api.sdk.file.FileApiClientImpl;
import com.smartling.api.sdk.file.FileType;
import com.smartling.api.sdk.file.parameters.FileLastModifiedParameterBuilder;
import com.smartling.api.sdk.file.parameters.FileListSearchParameterBuilder;
import com.smartling.api.sdk.file.parameters.FileUploadParameterBuilder;
import com.smartling.api.sdk.file.parameters.GetFileParameterBuilder;
import com.smartling.api.sdk.file.parameters.RetrievalType;
import com.smartling.api.sdk.file.response.EmptyResponse;
import com.smartling.api.sdk.file.response.FileList;
import com.smartling.api.sdk.file.response.FileStatus;

import java.io.File;

public class SmartlingApiExample
{
    private static final String   USER_ID       = "YOUR-USER-ID";
    private static final String   USER_SECRET   = "YOUR-USER-SECRET";
    private static final String   PROJECT_ID    = "YOUR-PROJECT-ID";
    private static final String   LOCALE        = "YOUR-LOCALE";

    private static final String   PATH_TO_FILE  = "resources/test.properties";
    private static final String   FILE_ENCODING = "UTF-8";
    private static final FileType FILE_TYPE     = FileType.JAVA_PROPERTIES;
    private static final String   CALLBACK_URL  = null;

    public static void main(String args[]) throws SmartlingApiException
    {
        FileApiClient smartlingFAPI = new FileApiClientImpl.Builder(PROJECT_ID).authWithUserIdAndSecret(USER_ID, USER_SECRET).build();

        // upload the file
        File file = new File(PATH_TO_FILE);
        FileUploadParameterBuilder fileUploadParameterBuilder = new FileUploadParameterBuilder(FILE_TYPE, getFileUri(file));
        fileUploadParameterBuilder
                .charset(FILE_ENCODING)
                .approveContent(false)
                .callbackUrl(CALLBACK_URL);
        UploadFileData uploadFileResponse = smartlingFAPI.uploadFile(file, fileUploadParameterBuilder);
        System.out.println(uploadFileResponse);

        // get last modified date
        FileLastModified lastModifiedResponse = smartlingFAPI.getLastModified(new FileLastModifiedParameterBuilder(getFileUri(file)).locale(LOCALE));
        System.out.println(lastModifiedResponse);

        // rename the file
        final String fileIdentifier = "myTestFileIdentifier";
        EmptyResponse renameFileResponse = smartlingFAPI.renameFile(getFileUri(file), fileIdentifier);
        System.out.println(renameFileResponse);

        // run a search for files
        FileList filesListResponse = smartlingFAPI.getFilesList(new FileListSearchParameterBuilder().withUriMask(fileIdentifier));
        System.out.println(filesListResponse);

        // check the file status
        FileStatus fileStatusResponse = smartlingFAPI.getFileStatus(fileIdentifier);
        System.out.println(fileStatusResponse);

        // get the file back, including any translations that have been published.
        StringResponse translatedContent = smartlingFAPI.getFile(new GetFileParameterBuilder(fileIdentifier, LOCALE).retrievalType(RetrievalType.PUBLISHED));
        System.out.println(translatedContent.getContents());

        // delete the file
        EmptyResponse deleteFileResponse = smartlingFAPI.deleteFile(fileIdentifier);
        System.out.println(deleteFileResponse);
    }

    private static String getFileUri(File file)
    {
        return file.getName();
    }
}
