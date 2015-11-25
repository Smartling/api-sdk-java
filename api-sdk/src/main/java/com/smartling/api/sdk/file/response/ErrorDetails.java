package com.smartling.api.sdk.file.response;

import com.smartling.web.api.v2.Details;

public class ErrorDetails extends Details
{
    private String field;
    private String errorId;

    public String getField()
    {
        return field;
    }

    public void setField(String field)
    {
        this.field = field;
    }

    public String getErrorId()
    {
        return errorId;
    }

    public void setErrorId(String errorId)
    {
        this.errorId = errorId;
    }

    @Override public String toString()
    {
        return "ErrorDetails{" +
                "field='" + field + '\'' +
                ", errorId='" + errorId + '\'' +
                '}';
    }
}
