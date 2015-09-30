package com.smartling.api.sdk.exceptions;

import java.util.List;

public class ResourceLockedException extends ApiException
{
    ResourceLockedException(final String contents, final List<String> messages, final int httpCode)
    {
        super(contents, messages, httpCode);
    }
}
