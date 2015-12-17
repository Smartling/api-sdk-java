package com.smartling.api.sdk.file.parameters;

import com.smartling.api.sdk.file.FileApiParams;
import com.smartling.api.sdk.file.RetrievalType;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.LinkedList;
import java.util.List;

import static com.smartling.api.sdk.file.FileApiParams.INCLUDE_ORIGINAL_STRINGS;
import static com.smartling.api.sdk.file.FileApiParams.LOCALE;
import static com.smartling.api.sdk.file.FileApiParams.RETRIEVAL_TYPE;

public class GetFileParameterBuilder implements ParameterBuilder
{
    private String fileUri;
    private String locale;
    private RetrievalType retrievalType;
    private Boolean includeOriginalStrings;

    public String getFileUri()
    {
        return fileUri;
    }

    public String getLocale()
    {
        return locale;
    }

    public RetrievalType getRetrievalType()
    {
        return retrievalType;
    }

    public Boolean getIncludeOriginalStrings()
    {
        return includeOriginalStrings;
    }

    /**
     * fileUri the identifier of the file
     * @param fileUri file URI
     * @return this builder
     */
    public GetFileParameterBuilder fileUri(String fileUri)
    {
        this.fileUri = fileUri;
        return this;
    }

    /**
     * locale the locale to retrieve the translation for, or null to request the original file.
     * @param locale locale code
     * @return this builder
     */
    public GetFileParameterBuilder locale(String locale)
    {
        this.locale = locale;
        return this;
    }

    /**
     * retrievalType flag indicating the type of file retrieval being requested. Can be null.
     * @param retrievalType retrieval type
     * @return this builder
     */
    public GetFileParameterBuilder retrievalType(RetrievalType retrievalType)
    {
        this.retrievalType = retrievalType;
        return this;
    }

    /**
     * include original strings
     * @param includeOriginalStrings whether to include original string in file
     * @return this builder
     */
    public GetFileParameterBuilder includeOriginalStrings(Boolean includeOriginalStrings)
    {
        this.includeOriginalStrings = includeOriginalStrings;
        return this;
    }

    @Override
    public List<NameValuePair> getNameValueList()
    {
        final List<NameValuePair> paramsList = new LinkedList<NameValuePair>();

        paramsList.add(new BasicNameValuePair(FileApiParams.FILE_URI, fileUri));
        paramsList.add(new BasicNameValuePair(RETRIEVAL_TYPE, null == retrievalType ? null : retrievalType.name()));
        paramsList.add(new BasicNameValuePair(LOCALE, locale));
        paramsList.add(new BasicNameValuePair(INCLUDE_ORIGINAL_STRINGS, null == includeOriginalStrings ? null : includeOriginalStrings.toString()));

        return paramsList;
    }
}
