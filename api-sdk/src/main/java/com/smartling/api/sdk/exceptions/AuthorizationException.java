package com.smartling.api.sdk.exceptions;

import java.util.List;

public class AuthorizationException extends ApiException
{
    public AuthorizationException(final String contents, final List<String> messages)
    {
        super(contents, messages);
    }
}
