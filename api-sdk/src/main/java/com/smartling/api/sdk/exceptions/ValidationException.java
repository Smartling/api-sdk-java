package com.smartling.api.sdk.exceptions;

import java.util.List;

public class ValidationException extends ApiException
{
    ValidationException(final String contents, List<String> messages, int httpCode)
    {
        super(contents, messages, httpCode);
    }
}
