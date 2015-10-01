package com.smartling.api.sdk.exceptions;

import java.util.List;

public class NotAvailableException extends ApiException
{
    public NotAvailableException(final String contents, final List<String> messages)
    {
        super(contents, messages);
    }
}
