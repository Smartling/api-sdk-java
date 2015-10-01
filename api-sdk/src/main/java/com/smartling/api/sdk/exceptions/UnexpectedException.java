package com.smartling.api.sdk.exceptions;

import java.util.List;

public class UnexpectedException extends ApiException
{
    public UnexpectedException(final String contents, final List<String> messages)
    {
        super(contents, messages);
    }
}
