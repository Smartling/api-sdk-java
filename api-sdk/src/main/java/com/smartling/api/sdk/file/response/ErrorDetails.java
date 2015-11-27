package com.smartling.api.sdk.file.response;

public class ErrorDetails
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
