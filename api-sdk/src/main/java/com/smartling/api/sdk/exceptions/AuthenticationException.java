package com.smartling.api.sdk.exceptions;

import java.util.List;

public class AuthenticationException extends ApiException
{
    public AuthenticationException(final String contents, final List<String> messages)
    {
        super(contents, messages);
    }
}
