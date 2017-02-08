package com.smartling.api.sdk.auth;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

class AuthenticationCommand
{
    private final String userIdentifier;
    private final String userSecret;

    AuthenticationCommand(String userIdentifier, String userSecret)
    {
        this.userIdentifier = userIdentifier;
        this.userSecret = userSecret;
    }

    public String getUserIdentifier()
    {
        return userIdentifier;
    }

    public String getUserSecret()
    {
        return userSecret;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        final AuthenticationCommand command = (AuthenticationCommand)o;

        return new EqualsBuilder()
                .append(userIdentifier, command.getUserIdentifier())
                .append(userSecret, command.getUserSecret())
                .isEquals();
     }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder()
                .append(userIdentifier)
                .append(userSecret)
                .hashCode();
    }

    @Override
    public String toString()
    {
        return "AuthenticationCommand{" +
                "userIdentifier='" + userIdentifier + '\'' +
                ", userSecret='" + userSecret + '\'' +
                '}';
    }
}
