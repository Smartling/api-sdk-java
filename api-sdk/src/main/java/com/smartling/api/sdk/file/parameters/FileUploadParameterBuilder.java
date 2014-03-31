package com.smartling.api.sdk.file.parameters;

import com.smartling.api.sdk.file.FileApiParams;
import com.smartling.api.sdk.file.FileType;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.*;

/**
 * Uploads a file for translation to the Smartling Translation API params
 */
public class FileUploadParameterBuilder implements ParameterBuilder
{
    private FileType fileType;
    private String fileUri;
    private Boolean approveContent;
    private String callbackUrl;
    private List<String> localesToApprove;
    private Boolean overwriteApprovedLocales;
    private Map<String, String> directives;

    /**
     * Directives a Map of smartling directives. Can be null
     * @param directives
     * @return
     */
    public FileUploadParameterBuilder directives(Map<String, String> directives)
    {
        this.directives = directives;
        return this;
    }

    /**
     * TRUE by default, it means that all locales in list will be approved everything
     * else excluded. If set to FALSE - no locales will be excluded, only existing and
     * locales which in the list will be approved. Can be null
     * @param overwriteApprovedLocales
     * @return
     */
    public FileUploadParameterBuilder overwriteApprovedLocales(Boolean overwriteApprovedLocales)
    {
        this.overwriteApprovedLocales = overwriteApprovedLocales;
        return this;
    }

    /**
     * List of locales to be approved after uploading the file. Can be null
     * @param localesToApprove
     * @return
     */
    public FileUploadParameterBuilder localesToApprove(List<String> localesToApprove)
    {
        this.localesToApprove = localesToApprove;
        return this;
    }

    public List<String> getLocalesToApprove()
    {
        return null != localesToApprove ? localesToApprove : new ArrayList<String>();
    }

    /**
     * Callback url. Can be null
     * @param callbackUrl
     * @return
     */
    public FileUploadParameterBuilder callbackUrl(String callbackUrl)
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
    public FileUploadParameterBuilder approveContent(Boolean approveContent)
    {
        this.approveContent = approveContent;
        return this;
    }

    /**
     * the identifier of the file
     * @param fileUri
     * @return
     */
    public FileUploadParameterBuilder fileUri(String fileUri)
    {
        this.fileUri = fileUri;
        return this;
    }

    public String getFileUri()
    {
        return fileUri;
    }

    /**
     * the type of file to upload
     * @param fileType
     * @return
     */
    public FileUploadParameterBuilder fileType(FileType fileType)
    {
        this.fileType = fileType;
        return this;
    }

    @Override
    public List<NameValuePair> getNameValueList()
    {
        final List<NameValuePair> paramsList = new LinkedList<NameValuePair>();

        paramsList.add(new BasicNameValuePair(FileApiParams.FILE_URI, fileUri));
        paramsList.add(new BasicNameValuePair(FileApiParams.FILE_TYPE, fileType.getIdentifier()));
        paramsList.add(new BasicNameValuePair(FileApiParams.APPROVED, null == approveContent ? null : Boolean.toString(approveContent)));
        paramsList.add(new BasicNameValuePair(FileApiParams.CALLBACK_URL, callbackUrl));
        if (localesToApprove != null && !localesToApprove.isEmpty())
            paramsList.addAll(convertLocalesBasedApproveParams(FileApiParams.LOCALES_TO_APPROVE, localesToApprove));
        if (overwriteApprovedLocales != null)
            paramsList.add(new BasicNameValuePair(FileApiParams.OVERWRITE_APPROVED_LOCALES, overwriteApprovedLocales.toString()));

        paramsList.addAll(convertMapParams(directives));

        return paramsList;
    }

    private List<NameValuePair> convertLocalesBasedApproveParams(String prefix, List<String> values)
    {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        for (int index = 0; index < values.size(); index++) {
            nameValuePairs.add(new BasicNameValuePair(prefix + "[" + index + "]", values.get(index)));
        }

        return nameValuePairs;
    }

    private List<NameValuePair> convertMapParams(Map<String, String> paramMap)
    {
        if (paramMap != null && !paramMap.isEmpty())
        {
            final List<NameValuePair> nameValuePairs = new LinkedList<NameValuePair>();
            for (String key : paramMap.keySet())
                nameValuePairs.add(new BasicNameValuePair(key, paramMap.get(key)));

            return nameValuePairs;
        }
        return Collections.emptyList();
    }
}
