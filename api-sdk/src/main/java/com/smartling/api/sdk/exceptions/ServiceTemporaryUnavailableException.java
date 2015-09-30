package com.smartling.api.sdk.exceptions;

import java.util.List;

public class ServiceTemporaryUnavailableException extends ApiException
{
    ServiceTemporaryUnavailableException(final String contents, final List<String> messages,
            final int httpCode)
    {
        super(contents, messages, httpCode);
    }
}
