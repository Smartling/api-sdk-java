package com.smartling.api.sdk.exceptions;

import java.util.List;

public class ResourceLockedException extends ApiException
{
    public ResourceLockedException(final String contents, final List<String> messages)
    {
        super(contents, messages);
    }
}
