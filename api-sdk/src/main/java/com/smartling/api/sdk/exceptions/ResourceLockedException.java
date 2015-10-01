package com.smartling.api.sdk.exceptions;

import java.util.List;

public class ResourceLockedException extends NotAvailableException
{
    public ResourceLockedException(final String contents, final List<String> messages)
    {
        super(contents, messages);
    }
}
