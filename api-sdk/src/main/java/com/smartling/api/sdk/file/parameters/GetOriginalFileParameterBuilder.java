package com.smartling.api.sdk.file.parameters;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.LinkedList;
import java.util.List;

import static com.smartling.api.sdk.file.parameters.FileApiParameter.RETRIEVAL_TYPE;

public class GetOriginalFileParameterBuilder implements ParameterBuilder
{
    private String fileUri;
    private RetrievalType retrievalType;

    public GetOriginalFileParameterBuilder(String fileUri)
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

    /**
     * retrievalType flag indicating the type of file retrieval being requested. Can be null.
     * @param retrievalType
     * @return
     */
    public GetOriginalFileParameterBuilder retrievalType(RetrievalType retrievalType)
    {
        this.retrievalType = retrievalType;
        return this;
    }

    @Override
    public List<NameValuePair> getNameValueList()
    {
        final List<NameValuePair> paramsList = new LinkedList<NameValuePair>();

        paramsList.add(new BasicNameValuePair(FileApiParameter.FILE_URI, fileUri));
        paramsList.add(new BasicNameValuePair(RETRIEVAL_TYPE, null == retrievalType ? null : retrievalType.name()));

        return paramsList;
    }
}
