package com.smartling.api.sdk.file.parameters;

import com.smartling.api.sdk.file.FileType;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class FileImportParameterBuilder implements ParameterBuilder
{
    private FileType fileType;
    private String fileUri;
    private boolean overwrite;
    private String translationState;
    private File file;
    private String locale;
    private String charset;

    public FileImportParameterBuilder(File file, String locale, String charset, FileType fileType, String fileUri)
    {
        this.file = file;
        this.locale = locale;
        this.charset = charset;
        this.fileType = fileType;
        this.fileUri = fileUri;
    }

    public FileImportParameterBuilder directives(final boolean overwrite)
    {
        this.overwrite = overwrite;
        return this;
    }

    public FileImportParameterBuilder translationState(final String translationState)
    {
        this.translationState = translationState;
        return this;
    }

    public FileImportParameterBuilder overwrite(final boolean overwrite)
    {
        this.overwrite = overwrite;
        return this;
    }

    public FileType getFileType()
    {
        return fileType;
    }

    public String getFileUri()
    {
        return fileUri;
    }

    public boolean isOverwrite()
    {
        return overwrite;
    }

    public String getTranslationState()
    {
        return translationState;
    }

    public File getFile()
    {
        return file;
    }

    public String getLocale()
    {
        return locale;
    }

    public String getCharset()
    {
        return charset;
    }

    @Override
    public List<NameValuePair> getNameValueList()
    {
        final List<NameValuePair> paramsList = new LinkedList<NameValuePair>();

        paramsList.add(new BasicNameValuePair(FileApiParameter.FILE_URI, fileUri));
        paramsList.add(new BasicNameValuePair(FileApiParameter.FILE_TYPE, null == fileType ? null : fileType.name()));
        paramsList.add(new BasicNameValuePair("overwrite", Boolean.toString(overwrite)));
        paramsList.add(new BasicNameValuePair("translationState", translationState));

        return paramsList;
    }
}
