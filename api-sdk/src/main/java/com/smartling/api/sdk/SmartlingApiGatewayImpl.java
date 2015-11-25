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
 *  SmartlingApiGatewayImpl should be declared singleton in your application
 */
public class SmartlingApiGatewayImpl implements SmartlingApiGateway
{
    AuthApiClient authApiClient;
    private final FileApiClient fileApiClient;
    private volatile AuthenticationContext authenticationContext;
    private ProxyConfiguration proxyConfiguration;
    private String baseAuthApiUrl = "https://api.smartling.com";
    private String baseFileApiUrl = "https://api.smartling.com";
    private String userId;
    private String userSecret;
    private ConnectionConfig connectionConfig;
    final ScheduledExecutorService expireExecutor;

    /**
     * You should provide batch of configuration parameters
     * @param userId your hashed Identifier in Smartling
     * @param userSecret your security token
     * @param projectId you projectId provided by Smartling
     */
    public SmartlingApiGatewayImpl(String userId, String userSecret, String projectId)
    {
        authApiClient = new AuthApiClient();
        fileApiClient = new FileApiClient();
        this.proxyConfiguration = null;
        this.userId = userId;
        this.userSecret = userSecret;
        this.connectionConfig = new ConnectionConfig(proxyConfiguration, baseFileApiUrl, projectId);
        expireExecutor = Executors.newScheduledThreadPool(1);
    }

    /**
     * You could also add your proxy configuration if yo need it
     * @param proxyConfiguration proxy configuration
     */
    public SmartlingApiGatewayImpl(String userId, String userSecret, String projectId, ProxyConfiguration proxyConfiguration)
    {
        authApiClient = new AuthApiClient();
        fileApiClient = new FileApiClient();
        this.proxyConfiguration = proxyConfiguration;
        this.userId = userId;
        this.userSecret = userSecret;
        this.connectionConfig = new ConnectionConfig(proxyConfiguration, baseFileApiUrl, projectId);
        expireExecutor = Executors.newScheduledThreadPool(1);
    }

    public void setBaseAuthApiUrl(final String baseAuthApiUrl)
    {
        this.baseAuthApiUrl = baseAuthApiUrl;
    }

    public void setBaseFileApiUrl(final String baseFileApiUrl)
    {
        this.baseFileApiUrl = baseFileApiUrl;
        this.connectionConfig.setBaseFileApiUrl(baseFileApiUrl);
    }

    AuthenticationContext generateAuthenticationContext() throws SmartlingApiException
    {
        AuthenticationContext defenciveCopy = new AuthenticationContext(authenticationContext);
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

        return defenciveCopy;
    }

    @Override public UploadFileData uploadFile(File fileToUpload, String charset, FileUploadParameterBuilder builder) throws SmartlingApiException
    {
        return fileApiClient.uploadFile(generateAuthenticationContext(), fileToUpload, charset, builder, connectionConfig).retrieveData();
    }

    @Override public UploadFileData uploadFile(InputStream stream, String fileName, String charset, FileUploadParameterBuilder builder) throws SmartlingApiException
    {
        return fileApiClient.uploadFile(generateAuthenticationContext(), stream, fileName, charset, builder, connectionConfig).retrieveData();
    }

    @Override public void deleteFile(String fileUri) throws SmartlingApiException
    {
        fileApiClient.deleteFile(generateAuthenticationContext(), fileUri, connectionConfig);
    }

    @Override public void renameFile(String oldFileUri, String newFileUri) throws SmartlingApiException
    {
        fileApiClient.renameFile(generateAuthenticationContext(), oldFileUri, newFileUri, connectionConfig);
    }

    @Override public FileLastModified getLastModified(FileLastModifiedParameterBuilder fileLastModifiedParameterBuilder) throws SmartlingApiException
    {
        return fileApiClient.getLastModified(generateAuthenticationContext(), fileLastModifiedParameterBuilder, connectionConfig).retrieveData();
    }

    @Override public StringResponse getFile(String locale, GetFileParameterBuilder getFileParameterBuilder) throws SmartlingApiException
    {
        return fileApiClient.getFile(generateAuthenticationContext(), locale, getFileParameterBuilder, connectionConfig);
    }

    @Override public StringResponse getOriginalFile(GetOriginalFileParameterBuilder getOriginalFileParameterBuilder) throws SmartlingApiException
    {
        return fileApiClient.getOriginalFile(generateAuthenticationContext(), getOriginalFileParameterBuilder, connectionConfig);
    }

    @Override public FileList getFilesList(FileListSearchParameterBuilder fileListSearchParameterBuilder) throws SmartlingApiException
    {
        return fileApiClient.getFilesList(generateAuthenticationContext(), fileListSearchParameterBuilder, connectionConfig).retrieveData();
    }

    @Override public FileLocaleStatus getFileLocaleStatus(String fileUri, String locale) throws SmartlingApiException
    {
        return fileApiClient.getFileLocaleStatus(generateAuthenticationContext(), locale, fileUri, connectionConfig).retrieveData();
    }

    @Override public FileStatus getFileStatus(String fileUri) throws SmartlingApiException
    {
        return fileApiClient.getFileStatus(generateAuthenticationContext(), fileUri, connectionConfig).retrieveData();
    }

    @Override public FileImportSmartlingData importTranslations(File fileToUpload, String locale, String charsetName,
            FileImportParameterBuilder fileImportParameterBuilder) throws SmartlingApiException
    {
        return fileApiClient.importTranslations(generateAuthenticationContext(), fileToUpload, locale, charsetName, fileImportParameterBuilder, connectionConfig).retrieveData();
    }

    @Override public AuthorizedLocales getAuthorizedLocales(String fileUri) throws SmartlingApiException
    {
        return fileApiClient.getAuthorizedLocales(generateAuthenticationContext(), fileUri, connectionConfig).retrieveData();
    }

    @Override public void authorizeLocales(String fileUri, String... localeIds) throws SmartlingApiException
    {
        fileApiClient.authorizeLocales(generateAuthenticationContext(), fileUri, localeIds, connectionConfig).retrieveData();
    }

    @Override public void unAuthorizeLocales(String fileUri, String... localeIds) throws SmartlingApiException
    {
        fileApiClient.unAuthorizeLocales(generateAuthenticationContext(), fileUri, localeIds, connectionConfig).retrieveData();
    }
}
