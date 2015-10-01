package com.smartling.api.sdk.exceptions;

import java.util.List;

public class OperationsLimitExceeded extends ApiException
{
    public OperationsLimitExceeded(final String contents, final List<String> messages)
    {
        super(contents, messages);
    }
}
