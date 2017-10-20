package com.smartling.api.sdk.file.parameters;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.smartling.api.sdk.file.parameters.FileApiParameter.FILE_NAME_MODE;
import static com.smartling.api.sdk.file.parameters.FileApiParameter.FILE_URIS;
import static com.smartling.api.sdk.file.parameters.FileApiParameter.INCLUDE_ORIGINAL_STRINGS;
import static com.smartling.api.sdk.file.parameters.FileApiParameter.LOCALE_IDS;
import static com.smartling.api.sdk.file.parameters.FileApiParameter.LOCALE_MODE;
import static com.smartling.api.sdk.file.parameters.FileApiParameter.RETRIEVAL_TYPE;

public class GetFilesArchiveParameterBuilder implements ParameterBuilder
{
    private List<String> fileUris;
    private List<String> localeIds;
    private Boolean includeOriginalStrings;
    private RetrievalType retrievalType;
    private FileNameMode fileNameMode;
    private LocaleMode localeMode;

    public GetFilesArchiveParameterBuilder()
    {
    }

    public List<String> getFileUris()
    {
        return fileUris;
    }

    public List<String> getLocaleIds()
    {
        return localeIds;
    }

    public FileNameMode getFileNameMode()
    {
        return fileNameMode;
    }

    public LocaleMode getLocaleMode()
    {
        return localeMode;
    }

    public RetrievalType getRetrievalType()
    {
        return retrievalType;
    }

    public Boolean getIncludeOriginalStrings()
    {
        return includeOriginalStrings;
    }

    public GetFilesArchiveParameterBuilder files(List<String> fileUris)
    {
        this.fileUris = fileUris;
        return this;
    }

    public GetFilesArchiveParameterBuilder localeIds(List<String> localeIds)
    {
        this.localeIds = localeIds;
        return this;
    }

    /**
     * retrievalType flag indicating the type of file retrieval being requested. Can be null.
     * @param retrievalType retrieval type
     * @return this builder
     */
    public GetFilesArchiveParameterBuilder retrievalType(RetrievalType retrievalType)
    {
        this.retrievalType = retrievalType;
        return this;
    }

    /**
     * include original strings
     * @param includeOriginalStrings whether to include original string in file
     * @return this builder
     */
    public GetFilesArchiveParameterBuilder includeOriginalStrings(Boolean includeOriginalStrings)
    {
        this.includeOriginalStrings = includeOriginalStrings;
        return this;
    }

    public GetFilesArchiveParameterBuilder fileNameMode(FileNameMode fileNameMode)
    {
        this.fileNameMode = fileNameMode;
        return this;
    }

    public GetFilesArchiveParameterBuilder localeMode(LocaleMode localeMode)
    {
        this.localeMode = localeMode;
        return this;
    }

    @Override
    public List<NameValuePair> getNameValueList()
    {
        final List<NameValuePair> paramsList = new LinkedList<NameValuePair>();

        paramsList.addAll(convertFileUrisParams(FILE_URIS, fileUris));
        paramsList.addAll(convertFileUrisParams(LOCALE_IDS, localeIds));

        if (includeOriginalStrings != null)
        {
            paramsList.add(new BasicNameValuePair(INCLUDE_ORIGINAL_STRINGS, includeOriginalStrings.toString()));
        }

        if (retrievalType != null)
        {
            paramsList.add(new BasicNameValuePair(RETRIEVAL_TYPE, retrievalType.name()));
        }

        if (fileNameMode != null)
        {
            paramsList.add(new BasicNameValuePair(FILE_NAME_MODE, fileNameMode.name()));
        }

        if (localeMode != null)
        {
            paramsList.add(new BasicNameValuePair(LOCALE_MODE, localeMode.name()));
        }

        return paramsList;
    }

    private List<NameValuePair> convertFileUrisParams(final String prefix, final List<String> values)
    {
        if (values == null || values.isEmpty())
        {
            return Collections.emptyList();
        }

        final List<NameValuePair> nameValuePairs = new ArrayList<>();
        for (String value : values)
        {
            nameValuePairs.add(new BasicNameValuePair(prefix + "[]", value));
        }

        return nameValuePairs;
    }
}
