package com.smartling.api.sdk.exceptions;

import java.util.List;

public class ValidationException extends ApiException
{
    public ValidationException(final String contents, List<String> messages)
    {
        super(contents, messages);
    }
}
