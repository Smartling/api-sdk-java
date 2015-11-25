package com.smartling.api.sdk;

import com.smartling.api.sdk.auth.AuthApiClient;
import com.smartling.api.sdk.auth.AuthenticationCommand;
import com.smartling.api.sdk.auth.AuthenticationContext;
import com.smartling.api.sdk.dto.file.FileLastModified;
import com.smartling.api.sdk.dto.file.StringResponse;
import com.smartling.api.sdk.dto.file.UploadFileData;
import com.smartling.api.sdk.exceptions.SmartlingApiException;
import com.smartling.api.sdk.file.FileApiClient;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *  It's the class your looking for! All interactions with SmartlingApi should be done via calling methods of this class
 *  SmartlingApiManager should be declared singleton in your application
 */
public class SmartlingApiManager
{
    AuthApiClient authApiClient;
    private FileApiClient fileApiClient;
    private volatile AuthenticationContext authenticationContext;
    private ProxyConfiguration proxyConfiguration;
    private String baseAuthApiUrl;
    private String userId;
    private String userSecret;
    private ConnectionConfig connectionConfig;
    final ScheduledExecutorService expireExecutor;

    /**
     * You should provide batch of configuration parameters
     * @param userId your hashed Identifier in Smartling
     * @param userSecret your security token
     * @param projectId you projectId provided by Smartling
     * @param baseAuthApiUrl https://api.smartling.com by default
     * @param baseFileApiUrl https://api.smartling.com by default
     */
    public SmartlingApiManager(String userId, String userSecret, String projectId, String baseAuthApiUrl, String baseFileApiUrl)
    {
        authApiClient = new AuthApiClient();
        fileApiClient = new FileApiClient();
        this.proxyConfiguration = null;
        this.userId = userId;
        this.userSecret = userSecret;
        this.baseAuthApiUrl = baseAuthApiUrl;
        this.connectionConfig = new ConnectionConfig(proxyConfiguration, baseFileApiUrl, projectId);
        expireExecutor = Executors.newScheduledThreadPool(10);
    }

    /**
     * You could also add your proxy configuration if yo need it
     * @param proxyConfiguration proxy configuration
     */
    public SmartlingApiManager withProxy(ProxyConfiguration proxyConfiguration)
    {
        this.proxyConfiguration = proxyConfiguration;
        return this;
    }

    public AuthenticationContext generateAuthenticationContext() throws SmartlingApiException
    {
        if (authenticationContext == null)
        {
            synchronized (expireExecutor)
            {
                if (authenticationContext == null)
                {
                    authenticationContext = authApiClient.authenticate(new AuthenticationCommand(userId, userSecret), proxyConfiguration, baseAuthApiUrl).retrieveData();
                    expireExecutor.schedule(new Runnable()
                                            {
                                                @Override public void run()
                                                {
                                                    authenticationContext = null;
                                                }
                                            }, authenticationContext.getExpiresIn() - 1, TimeUnit.SECONDS
                    );
                }
            }
        }
        return authenticationContext;
    }

    public UploadFileData uploadFile(File fileToUpload, String charset, FileUploadParameterBuilder builder) throws SmartlingApiException
    {
        return fileApiClient.uploadFile(generateAuthenticationContext(), fileToUpload, charset, builder, connectionConfig).retrieveData();
    }

    public UploadFileData uploadFile(InputStream stream, String fileName, String charset, FileUploadParameterBuilder builder) throws SmartlingApiException
    {
        return fileApiClient.uploadFile(generateAuthenticationContext(), stream, fileName, charset, builder, connectionConfig).retrieveData();
    }

    public void deleteFile(String fileUri) throws SmartlingApiException
    {
        fileApiClient.deleteFile(generateAuthenticationContext(), fileUri, connectionConfig);
    }

    public void renameFile(String oldFileUri, String newFileUri) throws SmartlingApiException
    {
        fileApiClient.renameFile(generateAuthenticationContext(), oldFileUri, newFileUri, connectionConfig);
    }

    public FileLastModified getLastModified(FileLastModifiedParameterBuilder fileLastModifiedParameterBuilder) throws SmartlingApiException
    {
        return fileApiClient.getLastModified(generateAuthenticationContext(), fileLastModifiedParameterBuilder, connectionConfig).retrieveData();
    }

    public StringResponse getFile(String locale, GetFileParameterBuilder getFileParameterBuilder) throws SmartlingApiException
    {
        return fileApiClient.getFile(generateAuthenticationContext(), locale, getFileParameterBuilder, connectionConfig);
    }

    public StringResponse getOriginalFile(String locale, GetOriginalFileParameterBuilder getOriginalFileParameterBuilder) throws SmartlingApiException
    {
        return fileApiClient.getOriginalFile(generateAuthenticationContext(), getOriginalFileParameterBuilder, connectionConfig);
    }

    public FileList getFilesList(FileListSearchParameterBuilder fileListSearchParameterBuilder) throws SmartlingApiException
    {
        return fileApiClient.getFilesList(generateAuthenticationContext(), fileListSearchParameterBuilder, connectionConfig).retrieveData();
    }

    public FileLocaleStatus getFileLocaleStatus(String fileUri, String locale) throws SmartlingApiException
    {
        return fileApiClient.getFileLocaleStatus(generateAuthenticationContext(), locale, fileUri, connectionConfig).retrieveData();
    }

    public FileStatus getFileStatus( String fileUri) throws SmartlingApiException
    {
        return fileApiClient.getFileStatus(generateAuthenticationContext(), fileUri, connectionConfig).retrieveData();
    }

    public FileImportSmartlingData importTranslations(File fileToUpload, String locale, String charsetName,
                FileImportParameterBuilder fileImportParameterBuilder) throws SmartlingApiException
    {
        return fileApiClient.importTranslations(generateAuthenticationContext(), fileToUpload, locale, charsetName, fileImportParameterBuilder, connectionConfig).retrieveData();
    }

    public AuthorizedLocales getAuthorizedLocales(String fileUri) throws SmartlingApiException
    {
        return fileApiClient.getAuthorizedLocales(generateAuthenticationContext(), fileUri, connectionConfig).retrieveData();
    }

    public void authorizeLocales(String fileUri, String... localeIds) throws SmartlingApiException
    {
        fileApiClient.authorizeLocales(generateAuthenticationContext(), fileUri, localeIds, connectionConfig).retrieveData();
    }

    public void unAuthorizeLocales(String fileUri, String... localeIds) throws SmartlingApiException
    {
        fileApiClient.unAuthorizeLocales(generateAuthenticationContext(), fileUri, localeIds, connectionConfig).retrieveData();
    }
}
