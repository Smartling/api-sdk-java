package com.smartling.api.sdk;

import com.smartling.api.sdk.auth.AuthApiClient;
import com.smartling.api.sdk.auth.AuthenticationCommand;
import com.smartling.api.sdk.auth.AuthenticationContext;
import com.smartling.api.sdk.dto.file.FileLastModified;
import com.smartling.api.sdk.dto.file.StringResponse;
import com.smartling.api.sdk.dto.file.UploadFileData;
import com.smartling.api.sdk.exceptions.ApiException;
import com.smartling.api.sdk.file.FileApiClient;
import com.smartling.api.sdk.file.parameters.FileImportParameterBuilder;
import com.smartling.api.sdk.file.parameters.FileLastModifiedParameterBuilder;
import com.smartling.api.sdk.file.parameters.FileListSearchParameter;
import com.smartling.api.sdk.file.parameters.FileUploadParameterBuilder;
import com.smartling.api.sdk.file.parameters.GetFileParameterBuilder;
import com.smartling.api.sdk.file.parameters.GetOriginalFileParameterBuilder;
import com.smartling.api.sdk.file.response.AuthorizedLocales;
import com.smartling.api.sdk.file.response.FileImportData;
import com.smartling.api.sdk.file.response.FileList;
import com.smartling.api.sdk.file.response.FileLocaleStatus;
import com.smartling.api.sdk.file.response.FileStatus;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ApiManager
{
    private AuthApiClient authApiClient;
    private FileApiClient fileApiClient;
    private AuthenticationContext authenticationContext;
    private ProxyConfiguration proxyConfiguration;
    //ToDo move to properties file
    private String baseAuthApiUrl = "https://api.smartling.com";
    private String baseFileApiUrl = "https://api.smartling.com";
    private String userId;
    private String userSecret;
    private ScheduledThreadPoolExecutor executor;
    private ConnectionConfig connectionConfig;

    public ApiManager(ProxyConfiguration proxyConfiguration, String userId, String userSecret, String projectId)
    {
        this.proxyConfiguration = proxyConfiguration;
        this.userId = userId;
        this.userSecret = userSecret;
        this.connectionConfig = new ConnectionConfig(proxyConfiguration, baseFileApiUrl, projectId);
    }

    public AuthenticationContext initAuthenticationContext() throws ApiException
    {
        authenticationContext = authApiClient.authenticate(new AuthenticationCommand(userId, userSecret), proxyConfiguration, baseAuthApiUrl).getData();
        return authenticationContext;
    }

    //ToDo make it smart and save accessKey
    public UploadFileData uploadFile(File fileToUpload, String charset, FileUploadParameterBuilder builder) throws ApiException
    {
        return fileApiClient.uploadFile(initAuthenticationContext(), fileToUpload, charset, builder, connectionConfig).getData();
    }

    //ToDo fileName + fileUri in builder looks like duplication
    public UploadFileData uploadFile(InputStream stream, String fileName, String charset, FileUploadParameterBuilder builder) throws ApiException
    {
        return fileApiClient.uploadFile(initAuthenticationContext(), stream, fileName, charset, builder, connectionConfig).getData();
    }

    public void deleteFile(String fileUri) throws ApiException
    {
        fileApiClient.deleteFile(initAuthenticationContext(), fileUri, connectionConfig);
    }

    public void renameFile(String oldFileUri, String newFileUri) throws ApiException
    {
        fileApiClient.renameFile(initAuthenticationContext(), oldFileUri, newFileUri, connectionConfig);
    }

    public FileLastModified getLastModified(FileLastModifiedParameterBuilder fileLastModifiedParameterBuilder) throws ApiException
    {
        return fileApiClient.getLastModified(initAuthenticationContext(), fileLastModifiedParameterBuilder, connectionConfig).getData();
    }

    public StringResponse getFile(String locale, GetFileParameterBuilder getFileParameterBuilder) throws ApiException
    {
        return fileApiClient.getFile(initAuthenticationContext(), locale, getFileParameterBuilder, connectionConfig);
    }

    public StringResponse getOriginalFile(String locale, GetOriginalFileParameterBuilder getOriginalFileParameterBuilder) throws ApiException
    {
        return fileApiClient.getOriginalFile(initAuthenticationContext(), getOriginalFileParameterBuilder, connectionConfig);
    }

    //ToDo convert FileListSearchParameter to builder
    public FileList getFilesList(FileListSearchParameter fileListSearchParameter) throws ApiException
    {
        return fileApiClient.getFilesList(initAuthenticationContext(), fileListSearchParameter, connectionConfig).getData();
    }

    public FileLocaleStatus getFileLocaleStatus(String locale, String fileUri) throws ApiException
    {
        return fileApiClient.getFileLocaleStatus(initAuthenticationContext(), locale, fileUri, connectionConfig).getData();
    }

    public FileStatus getFileStatus( String fileUri) throws ApiException
    {
        return fileApiClient.getFileStatus(initAuthenticationContext(), fileUri, connectionConfig).getData();
    }

    public FileImportData importTranslations(File fileToUpload, String locale, String charsetName,
                FileImportParameterBuilder fileImportParameterBuilder) throws ApiException
    {
        return fileApiClient.importTranslations(initAuthenticationContext(), fileToUpload, locale, charsetName, fileImportParameterBuilder, connectionConfig).getData();
    }

    public AuthorizedLocales getAuthorizedLocales(String fileUri) throws ApiException
    {
        return fileApiClient.getAuthorizedLocales(initAuthenticationContext(), fileUri, connectionConfig).getData();
    }

    public void authorizeLocales(String fileUri, String... localeIds) throws ApiException
    {
        fileApiClient.authorizeLocales(initAuthenticationContext(), fileUri, localeIds, connectionConfig).getData();
    }

    public void unAuthorizeLocales(String fileUri, String... localeIds) throws ApiException
    {
        fileApiClient.unAuthorizeLocales(initAuthenticationContext(), fileUri, localeIds, connectionConfig).getData();
    }
}
