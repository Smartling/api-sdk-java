package com.smartling.api.sdk;

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
import com.smartling.api.sdk.file.response.FileImportSmartlingData;
import com.smartling.api.sdk.file.response.FileList;
import com.smartling.api.sdk.file.response.FileLocaleStatus;
import com.smartling.api.sdk.file.response.FileStatus;

import java.io.File;
import java.io.InputStream;

public interface SmartlingApiGateway
{
    UploadFileData uploadFile(File fileToUpload, String charset, FileUploadParameterBuilder builder) throws SmartlingApiException;

    UploadFileData uploadFile(InputStream stream, String fileName, String charset, FileUploadParameterBuilder builder) throws SmartlingApiException;

    void deleteFile(String fileUri) throws SmartlingApiException;

    void renameFile(String oldFileUri, String newFileUri) throws SmartlingApiException;

    FileLastModified getLastModified(FileLastModifiedParameterBuilder fileLastModifiedParameterBuilder) throws SmartlingApiException;

    StringResponse getFile(String locale, GetFileParameterBuilder getFileParameterBuilder) throws SmartlingApiException;

    StringResponse getOriginalFile(GetOriginalFileParameterBuilder getOriginalFileParameterBuilder) throws SmartlingApiException;

    FileList getFilesList(FileListSearchParameterBuilder fileListSearchParameterBuilder) throws SmartlingApiException;

    FileLocaleStatus getFileLocaleStatus(String fileUri, String locale) throws SmartlingApiException;

    FileStatus getFileStatus(String fileUri) throws SmartlingApiException;

    FileImportSmartlingData importTranslations(File fileToUpload, String locale, String charsetName,
            FileImportParameterBuilder fileImportParameterBuilder) throws SmartlingApiException;

    AuthorizedLocales getAuthorizedLocales(String fileUri) throws SmartlingApiException;

    void authorizeLocales(String fileUri, String... localeIds) throws SmartlingApiException;

    void unAuthorizeLocales(String fileUri, String... localeIds) throws SmartlingApiException;
}
