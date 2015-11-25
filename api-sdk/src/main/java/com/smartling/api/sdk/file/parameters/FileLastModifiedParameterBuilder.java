package com.smartling.api.sdk.file.parameters;

import com.smartling.api.sdk.util.DateFormatter;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class FileLastModifiedParameterBuilder implements ParameterBuilder
{
    private String fileUri;
    private Date lastModifiedAfter;
    private String locale;

    public FileLastModifiedParameterBuilder (String fileURI)
    {
        this.fileUri = fileURI;
    }

    public FileLastModifiedParameterBuilder lastModifiedAfter(Date lastModifiedAfter)
    {
        this.lastModifiedAfter = lastModifiedAfter;
        return this;
    }

    public FileLastModifiedParameterBuilder locale(String locale)
    {
        this.locale = locale;
        return this;
    }

    @Override public List<NameValuePair> getNameValueList()
    {
        final List<NameValuePair> paramsList = new LinkedList<NameValuePair>();

        paramsList.add(new BasicNameValuePair(FileApiParameter.FILE_URI, fileUri));

        if (StringUtils.isEmpty(locale)){
            paramsList.add(new BasicNameValuePair(FileApiParameter.LOCALE, locale));
        }
        if (lastModifiedAfter != null){
            paramsList.add(new BasicNameValuePair(FileApiParameter.LAST_MODIFIED_AFTER, DateFormatter.format(lastModifiedAfter)));
        }

        return paramsList;
    }
}
