package com.smartling.api.sdk.file.parameters;

import com.smartling.api.sdk.file.FileType;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Uploads a file for translation to the Smartling Translation API params
 */
public class FileUploadParameterBuilder implements ParameterBuilder
{
    private FileType fileType;
    private String fileUri;
    private Boolean approveContent;
    private String callbackUrl;
    private String clientUid;
    private List<String> localeIdsToAuthorize;
    private Boolean overwriteAuthorizedLocales;
    private Map<String, String> directives;
    private String charset;

    public FileUploadParameterBuilder (FileType fileType, String fileUri)
    {
        this.fileType = fileType;
        this.fileUri = fileUri;
    }

    /**
     * Directives a Map of smartling directives. Can be null
     * @param directives
     * @return
     */
    public FileUploadParameterBuilder directives(final Map<String, String> directives)
    {
        this.directives = directives;
        return this;
    }

    /**
     * TRUE by default, it means that all locales in list will be approved everything
     * else excluded. If set to FALSE - no locales will be excluded, only existing and
     * locales which in the list will be approved. Can be null
     * @param overwriteAuthorizedLocales
     * @return
     */
    public FileUploadParameterBuilder overwriteAuthorizedLocales(final Boolean overwriteAuthorizedLocales)
    {
        this.overwriteAuthorizedLocales = overwriteAuthorizedLocales;
        return this;
    }

    /**
     * List of locales to be approved after uploading the file. Can be null
     * @param localesToApprove
     * @return
     */
    public FileUploadParameterBuilder localeIdsToAuthorize(final List<String> localesToApprove)
    {
        this.localeIdsToAuthorize = localesToApprove;
        return this;
    }

    public List<String> getLocaleIdsToAuthorize()
    {
        return null != localeIdsToAuthorize ? localeIdsToAuthorize : new ArrayList<String>();
    }

    /**
     * Callback url. Can be null
     * @param callbackUrl
     * @return
     */
    public FileUploadParameterBuilder callbackUrl(final String callbackUrl)
    {
        this.callbackUrl = callbackUrl;
        return this;
    }

    /**
     * TRUE if the file contents should be approved after uploading the file. Can be NULL.
     * NULL uses fileApi default of FALSE.
     * @param approveContent
     * @return
     */
    public FileUploadParameterBuilder approveContent(final Boolean approveContent)
    {
        this.approveContent = approveContent;
        return this;
    }

    public String getFileUri()
    {
        return fileUri;
    }

    /**
     * library UID that uses sdk for sending files
     * if UID is not provided default UID that is equal to sdk library UID is set
     * @param name
     * @param version
     * @return
     */
    public FileUploadParameterBuilder clientUid(final String name, final String version)
    {
        this.clientUid = ProjectPropertiesHolder.clientUid(name, version);
        return this;
    }

    public FileUploadParameterBuilder charset(final String charset)
    {
        this.charset = charset;
        return this;
    }

    public String getCharset()
    {
        return charset;
    }

    public FileType getFileType()
    {
        return fileType;
    }

    @Override
    public List<NameValuePair> getNameValueList()
    {
        final List<NameValuePair> paramsList = new LinkedList<NameValuePair>();

        paramsList.add(new BasicNameValuePair(FileApiParameter.FILE_URI, fileUri));
        paramsList.add(new BasicNameValuePair(FileApiParameter.FILE_TYPE, fileType.getIdentifier()));
        paramsList.add(new BasicNameValuePair(FileApiParameter.APPROVED, null == approveContent ? null : Boolean.toString(approveContent)));
        paramsList.add(new BasicNameValuePair(FileApiParameter.CALLBACK_URL, callbackUrl));
        if (localeIdsToAuthorize != null && !localeIdsToAuthorize.isEmpty())
            paramsList.addAll(convertLocalesBasedApproveParams(FileApiParameter.LOCALES_ID_TO_AUTHORIZE, localeIdsToAuthorize));
        if (overwriteAuthorizedLocales != null)
            paramsList.add(new BasicNameValuePair(FileApiParameter.OVERWRITE_AUTHORIZED_LOCALES, overwriteAuthorizedLocales.toString()));
        paramsList.add(new BasicNameValuePair(FileApiParameter.CLIENT_LIB_ID, clientUid != null ? clientUid : ProjectPropertiesHolder.defaultClientUid()));

        paramsList.addAll(convertMapParams(directives));

        return paramsList;
    }

    private List<NameValuePair> convertLocalesBasedApproveParams(final String prefix, final List<String> values)
    {
        if (values == null || values.isEmpty())
            return Collections.emptyList();

        final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        for (int index = 0; index < values.size(); index++) {
            nameValuePairs.add(new BasicNameValuePair(prefix + "[]", values.get(index)));
        }

        return nameValuePairs;
    }

    private List<NameValuePair> convertMapParams(final Map<String, String> paramMap)
    {
        if (paramMap != null && !paramMap.isEmpty())
        {
            final List<NameValuePair> nameValuePairs = new LinkedList<NameValuePair>();
            for (final String key : paramMap.keySet())
                nameValuePairs.add(new BasicNameValuePair(key, paramMap.get(key)));

            return nameValuePairs;
        }
        return Collections.emptyList();
    }
}
