package com.smartling.api.sdk.auth;

public class AuthenticationCommand
{
    private final String userIdentifier;
    private final String userSecret;

    public AuthenticationCommand(String userIdentifier, String userSecret)
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

    @Override public boolean equals(final Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        final AuthenticationCommand command = (AuthenticationCommand)o;

        if (userIdentifier != null ? !userIdentifier.equals(command.userIdentifier) : command.userIdentifier != null)
            return false;
        return !(userSecret != null ? !userSecret.equals(command.userSecret) : command.userSecret != null);

    }

    @Override public int hashCode()
    {
        int result = userIdentifier != null ? userIdentifier.hashCode() : 0;
        result = 31 * result + (userSecret != null ? userSecret.hashCode() : 0);
        return result;
    }

    @Override public String toString()
    {
        return "AuthenticationCommand{" +
                "userIdentifier='" + userIdentifier + '\'' +
                ", userSecret='" + userSecret + '\'' +
                '}';
    }
}
