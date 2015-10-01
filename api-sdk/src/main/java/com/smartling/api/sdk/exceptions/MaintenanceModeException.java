package com.smartling.api.sdk.exceptions;

import java.util.List;

public class MaintenanceModeException extends NotAvailableException
{
    public MaintenanceModeException(final String contents, final List<String> messages)
    {
        super(contents, messages);
    }
}
