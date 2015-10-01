package com.smartling.api.sdk.exceptions;

import java.util.List;

public class ServiceTemporaryUnavailableException extends ApiException
{
    public ServiceTemporaryUnavailableException(final String contents, final List<String> messages)
    {
        super(contents, messages);
    }
}
