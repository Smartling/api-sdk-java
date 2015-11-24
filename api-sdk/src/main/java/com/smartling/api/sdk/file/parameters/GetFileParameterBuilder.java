package com.smartling.api.sdk.file.parameters;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.LinkedList;
import java.util.List;

import static com.smartling.api.sdk.file.parameters.FileApiParameter.INCLUDE_ORIGINAL_STRINGS;
import static com.smartling.api.sdk.file.parameters.FileApiParameter.RETRIEVAL_TYPE;

public class GetFileParameterBuilder implements ParameterBuilder
{
    private String fileUri;
    private RetrievalType retrievalType;
    private Boolean includeOriginalStrings;

    public GetFileParameterBuilder(String fileUri)
    {
        this.fileUri = fileUri;
    }

    public String getFileUri()
    {
        return fileUri;
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
     * retrievalType flag indicating the type of file retrieval being requested. Can be null.
     * @param retrievalType
     * @return
     */
    public GetFileParameterBuilder retrievalType(RetrievalType retrievalType)
    {
        this.retrievalType = retrievalType;
        return this;
    }

    /**
     * include original strings
     * @param includeOriginalStrings
     * @return
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

        paramsList.add(new BasicNameValuePair(FileApiParameter.FILE_URI, fileUri));
        paramsList.add(new BasicNameValuePair(RETRIEVAL_TYPE, null == retrievalType ? null : retrievalType.name()));
        paramsList.add(new BasicNameValuePair(INCLUDE_ORIGINAL_STRINGS, null == includeOriginalStrings ? null : includeOriginalStrings.toString()));

        return paramsList;
    }
}
