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
package com.smartling.api.sdk;

import com.smartling.api.sdk.exceptions.ApiException;
import com.smartling.api.sdk.file.FileListSearchParams;
import com.smartling.api.sdk.file.RetrievalType;
import com.smartling.api.sdk.file.parameters.FileUploadParameterBuilder;
import com.smartling.api.sdk.file.parameters.GetFileParameterBuilder;
import com.smartling.api.sdk.dto.ApiResponse;
import com.smartling.api.sdk.dto.EmptyResponse;
import com.smartling.api.sdk.dto.file.FileLastModified;
import com.smartling.api.sdk.dto.file.FileList;
import com.smartling.api.sdk.dto.file.FileStatus;
import com.smartling.api.sdk.dto.file.StringResponse;
import com.smartling.api.sdk.dto.file.UploadFileData;

import java.io.File;
import java.io.InputStream;
import java.util.Date;

/**
 * Communication point for interacting files with the Smartling Translation API.
 */
// TODO(AShesterov): why is it actually called "adapter"?? It's not a client adapter, but a client.
public interface FileApiClientAdapter
{
    /**
     * Uploads a file for translation to the Smartling Translation API.
     *
     * @param fileToUpload the file that is to be uploaded.
     * @param fileEncoding the encoding of the file. Can be null but best if encoding is specified.
     * @param fileUploadParameterBuilder  parameters
     * @return ApiResponse from a success response from the File API.
     * @throws ApiException if an exception has occurred or non success is returned from the Smartling Translation API.
     */
    ApiResponse<UploadFileData> uploadFile(final File fileToUpload, final String fileEncoding,
                                       final FileUploadParameterBuilder fileUploadParameterBuilder) throws ApiException;

    /**
     * Uploads a file for translation to the Smartling Translation API.
     *
     * @param inputStream stream to be read from to collect data and then upload as a file.
     * @param fileName name used to identify the file
     * @param fileEncoding the encoding of the file. Can be null but best if encoding is specified.
     * @param fileUploadParameterBuilder  parameters
     * @return ApiResponse from a success response from the File API.
     * @throws ApiException if an exception has occurred or non success is returned from the Smartling Translation API.
     */
    ApiResponse<UploadFileData> uploadFile(final InputStream inputStream, final String fileName, final String fileEncoding,
                                       final FileUploadParameterBuilder fileUploadParameterBuilder) throws ApiException;

    /**
     * Get the translated (or original) file contents.
     *
     * @param fileUri the identifier of the file
     * @param locale the locale to retrieve the translation for, or null to request the original file.
     * @param retrievalType flag indicating the type of file retrieval being requested. Can be null.
     * @return {@link StringResponse} the contents of the requested file along with the encoding of the file.
     * @throws ApiException if an exception has occurred or non success is returned from the Smartling Translation API.
     */
    StringResponse getFile(String fileUri, String locale, RetrievalType retrievalType) throws ApiException;

    /**
     * Get the translated (or original) file contents.
     * @param getFileParameterBuilder - params builder
     * @return {@link StringResponse} the contents of the requested file along with the encoding of the file.
     * @throws ApiException if an exception has occurred or non success is returned from the Smartling Translation API.
     */
    StringResponse getFile(GetFileParameterBuilder getFileParameterBuilder) throws ApiException;

    /**
     * Get the listing of translated files for the specified locale.
     *
     * @param fileListSearchParams the search parameters to use when querying for a list of files.
     * @return ApiResponse from a success response from the File API.
     * @throws ApiException if an exception has occurred or non success is returned from the Smartling Translation API.
     */
    ApiResponse<FileList> getFilesList(FileListSearchParams fileListSearchParams) throws ApiException;

    /**
     * Get the status of a file for the specified locale
     *
     * @param fileUri the identifier of the file
     * @param locale the locale
     * @return ApiResponse from a success response from the File API.
     * @throws ApiException if an exception has occurred or non success is returned from the Smartling Translation API.
     */
    ApiResponse<FileStatus> getFileStatus(String fileUri, String locale) throws ApiException;

    /**
     * Delete the specified file
     *
     * @param fileUri the identifier of the file
     * @return ApiResponse from a successful delete of from the File API.
     * @throws ApiException if an exception has occurred or non success is returned from the Smartling Translation API.
     */
    ApiResponse<EmptyResponse> deleteFile(String fileUri) throws ApiException;

    /**
     * Rename the file with the specified fileUri to the newFileUri
     *
     * @param fileUri current fileUri
     * @param newFileUri requested fileUri
     * @return {@link ApiResponse} for a successful deletion.
     * @throws ApiException if a non success is returned from the service.
     */
    ApiResponse<EmptyResponse> renameFile(String fileUri, String newFileUri) throws ApiException;

    /**
     * Returns information about when a file was last modified for a particular locale, and allows you to filter by lastModified date and/or locale so that you can download only those files changed
     * since their last download.
     *
     * @param fileUri current fileUri
     * @param lastModifiedAfter an optional filter that limits the return to only those file and locale combinations that have a lastModified date after the parameter lastModifiedAfter. The items
     * array will be empty if the file has not been modified in any of the locales since the lastModifiedAfter date specified
     * @param locale an optional filter that will limit the locales checked to only the specified locale
     * @return {@link ApiResponse} from a success response from the File API.
     * @throws ApiException if an exception has occurred or non success is returned from the Smartling Translation API.
     */
    ApiResponse<FileLastModified> getLastModified(String fileUri, Date lastModifiedAfter, String locale) throws ApiException;
}
