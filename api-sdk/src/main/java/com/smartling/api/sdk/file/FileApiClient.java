package com.smartling.api.sdk.file;

import com.smartling.api.sdk.auth.TokenProvider;
import com.smartling.api.sdk.dto.file.FileLastModified;
import com.smartling.api.sdk.dto.file.StringResponse;
import com.smartling.api.sdk.dto.file.UploadFileData;
import com.smartling.api.sdk.exceptions.SmartlingApiException;
import com.smartling.api.sdk.file.parameters.FileImportParameterBuilder;
import com.smartling.api.sdk.file.parameters.FileLastModifiedParameterBuilder;
import com.smartling.api.sdk.file.parameters.FileListSearchParameterBuilder;
import com.smartling.api.sdk.file.parameters.FileUploadParameterBuilder;
import com.smartling.api.sdk.file.parameters.GetFileParameterBuilder;
import com.smartling.api.sdk.file.parameters.GetOriginalFileParameterBuilder;
import com.smartling.api.sdk.file.response.AuthorizedLocales;
import com.smartling.api.sdk.file.response.EmptyResponse;
import com.smartling.api.sdk.file.response.FileImportSmartlingData;
import com.smartling.api.sdk.file.response.FileList;
import com.smartling.api.sdk.file.response.FileLocaleStatus;
import com.smartling.api.sdk.file.response.FileStatus;

import java.io.File;
import java.io.InputStream;

public interface FileApiClient
{
    void setTokenProvider(TokenProvider tokenProvider);

    void setClientLibName(String clientLibName);

    void setClientLibVersion(String clientLibVersion);

    UploadFileData uploadFile(File fileToUpload, FileUploadParameterBuilder fileUploadParameterBuilder) throws SmartlingApiException;

    UploadFileData uploadFile(InputStream inputStream, String fileName, FileUploadParameterBuilder fileUploadParameterBuilder)
            throws SmartlingApiException;

    EmptyResponse deleteFile(String fileUri) throws SmartlingApiException;

    EmptyResponse renameFile(String fileUri, String newFileUri) throws SmartlingApiException;

    FileLastModified getLastModified(FileLastModifiedParameterBuilder builder) throws SmartlingApiException;

    StringResponse getFile(GetFileParameterBuilder getFileParameterBuilder) throws SmartlingApiException;

    StringResponse getOriginalFile(GetOriginalFileParameterBuilder getFileParameterBuilder) throws SmartlingApiException;

    FileList getFilesList(FileListSearchParameterBuilder fileListSearchParameterBuilder) throws SmartlingApiException;

    FileLocaleStatus getFileLocaleStatus(String fileUri, String locale) throws SmartlingApiException;

    FileStatus getFileStatus(String fileUri) throws SmartlingApiException;

    FileImportSmartlingData importTranslations(FileImportParameterBuilder fileImportParameterBuilder)
                    throws SmartlingApiException;

    AuthorizedLocales getAuthorizedLocales(String fileUri) throws SmartlingApiException;

    EmptyResponse authorizeLocales(String fileUri, String... localeIds) throws SmartlingApiException;

    EmptyResponse unAuthorizeLocales(String fileUri, String... localeIds) throws SmartlingApiException;
}
